package kr.hhplus.be.server.usecase.payment.interactor;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.event.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatClass;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.infrastructure.persistence.payment.PaymentManager;
import kr.hhplus.be.server.infrastructure.persistence.payment.PaymentTransactionResult;
import kr.hhplus.be.server.infrastructure.persistence.queue.QueueTokenManager;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.payment.input.PaymentCommand;
import kr.hhplus.be.server.usecase.payment.output.PaymentOutput;
import kr.hhplus.be.server.usecase.payment.output.PaymentResult;

@ExtendWith(MockitoExtension.class)
class PaymentInteractorTest {

	@InjectMocks
	private PaymentInteractor paymentInteractor;

	@Mock
	private PaymentOutput paymentOutput;

	@Mock
	private PaymentManager paymentManager;

	@Mock
	private EventPublisher eventPublisher;

	@Mock
	private DistributedLockManager distributedLockManager;

	@Mock
	private QueueTokenManager queueTokenManager;

	private UUID reservationId;
	private UUID queueTokenId;
	private UUID userId;
	private UUID concertId;
	private UUID seatId;
	private UUID paymentId;
	private UUID concertDateId;
	private PaymentCommand paymentCommand;
	private PaymentTransactionResult paymentTransactionResult;
	private Payment payment;
	private User user;
	private Reservation reservation;
	private Seat seat;
	private QueueToken queueToken;

	@BeforeEach
	void beforeEach() {
		reservationId = UUID.randomUUID();
		queueTokenId = UUID.randomUUID();
		userId = UUID.randomUUID();
		concertId = UUID.randomUUID();
		seatId = UUID.randomUUID();
		paymentId = UUID.randomUUID();
		concertDateId = UUID.randomUUID();

		LocalDateTime now = LocalDateTime.now();
		paymentCommand = new PaymentCommand(reservationId, queueTokenId.toString());

		payment = new Payment(paymentId, userId, reservationId, BigDecimal.valueOf(10000), PaymentStatus.SUCCESS, null, now, now);
		user = new User(userId, BigDecimal.valueOf(90000), now, now);
		reservation = new Reservation(reservationId, userId, seatId, ReservationStatus.SUCCESS, now, now);
		seat = new Seat(seatId, concertDateId, 10, BigDecimal.valueOf(10000), SeatClass.VIP, SeatStatus.ASSIGNED, now, now);
		queueToken = QueueToken.activeTokenOf(queueTokenId, userId, concertId, 10000000L);

		paymentTransactionResult = new PaymentTransactionResult(payment, reservation, seat, user);
	}

	@Test
	@DisplayName("결제_성공")
	void payment_Success() throws Exception {
		when(queueTokenManager.getQueueToken(queueTokenId.toString())).thenReturn(queueToken);
		when(distributedLockManager.executeWithLockHasReturn(anyString(), any()))
			.thenAnswer(invocation -> {
				Callable<PaymentTransactionResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(paymentManager.processPayment(paymentCommand, queueToken))
			.thenReturn(paymentTransactionResult);
		
		paymentInteractor.payment(paymentCommand);

		verify(queueTokenManager, times(1)).getQueueToken(queueTokenId.toString());
		verify(distributedLockManager, times(2)).executeWithLockHasReturn(anyString(), any());
		verify(paymentManager, times(1)).processPayment(paymentCommand, queueToken);
		verify(eventPublisher, times(1)).publish(any(PaymentSuccessEvent.class));
		verify(paymentOutput, times(1)).ok(any(PaymentResult.class));
	}

	@Test
	@DisplayName("결제_실패_유저못찾음")
	void payment_Failure_UserNotFount() throws Exception {
		when(queueTokenManager.getQueueToken(queueTokenId.toString())).thenReturn(queueToken);
		when(distributedLockManager.executeWithLockHasReturn(anyString(), any()))
			.thenAnswer(invocation -> {
				Callable<PaymentTransactionResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(paymentManager.processPayment(paymentCommand, queueToken))
			.thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));
		
		CustomException customException = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

		verify(queueTokenManager, times(1)).getQueueToken(queueTokenId.toString());
		verify(distributedLockManager, times(2)).executeWithLockHasReturn(anyString(), any());
		verify(paymentManager, times(1)).processPayment(paymentCommand, queueToken);
		verify(eventPublisher, never()).publish(any());
		verify(paymentOutput, never()).ok(any());
	}

	@Test
	@DisplayName("결제_실패_잔액부족")
	void payment_Failure_InsufficientBalance() throws Exception {
		when(queueTokenManager.getQueueToken(queueTokenId.toString())).thenReturn(queueToken);
		when(distributedLockManager.executeWithLockHasReturn(anyString(), any()))
			.thenAnswer(invocation -> {
				Callable<PaymentTransactionResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(paymentManager.processPayment(paymentCommand, queueToken))
			.thenThrow(new CustomException(ErrorCode.INSUFFICIENT_BALANCE));
		
		CustomException customException = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_BALANCE);

		verify(queueTokenManager, times(1)).getQueueToken(queueTokenId.toString());
		verify(distributedLockManager, times(2)).executeWithLockHasReturn(anyString(), any());
		verify(paymentManager, times(1)).processPayment(paymentCommand, queueToken);
		verify(eventPublisher, never()).publish(any());
		verify(paymentOutput, never()).ok(any());
	}

	@Test
	@DisplayName("결제_실패_이미결제됨")
	void payment_Failure_AlreadyPaid() throws Exception {
		when(queueTokenManager.getQueueToken(queueTokenId.toString())).thenReturn(queueToken);
		when(distributedLockManager.executeWithLockHasReturn(anyString(), any()))
			.thenAnswer(invocation -> {
				Callable<PaymentTransactionResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(paymentManager.processPayment(paymentCommand, queueToken))
			.thenThrow(new CustomException(ErrorCode.ALREADY_PAID));
		
		CustomException customException = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_PAID);

		verify(queueTokenManager, times(1)).getQueueToken(queueTokenId.toString());
		verify(distributedLockManager, times(2)).executeWithLockHasReturn(anyString(), any());
		verify(paymentManager, times(1)).processPayment(paymentCommand, queueToken);
		verify(eventPublisher, never()).publish(any());
		verify(paymentOutput, never()).ok(any());
	}
}