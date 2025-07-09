package kr.hhplus.be.server.usecase.payment.interactor;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.payment.domain.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.queue.adapter.out.persistence.QueueApplicationService;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.payment.usecase.PaymentInteractor;
import kr.hhplus.be.server.domain.user.domain.User;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.aop.DistributedLockAspect;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.payment.port.in.PaymentCommand;
import kr.hhplus.be.server.domain.payment.dto.PaymentResult;

@ExtendWith(MockitoExtension.class)
public class PaymentDistributedLockTest {

	@InjectMocks
	private PaymentInteractor paymentInteractor;

	@Mock
	private PaymentService paymentService;

	@Mock
	private QueueApplicationService queueApplicationService;

	@Mock
	private PaymentOutput paymentOutput;

	@Mock
	private EventPublisher eventPublisher;

	@Mock
	private DistributedLockAspect distributedLockAspect;

	private PaymentCommand paymentCommand;
	private PaymentTransactionResult paymentTransactionResult;
	private QueueToken queueToken;

	private String userLockKey;
	private String reservationLockKey;
	private UUID reservationId;
	private UUID queueTokenId;
	private UUID userId;
	private UUID concertId;

	@BeforeEach
	void beforeEach() {
		reservationId = UUID.randomUUID();
		queueTokenId = UUID.randomUUID();
		userId = UUID.randomUUID();
		concertId = UUID.randomUUID();

		userLockKey = "user:" + userId;
		reservationLockKey = "reservation:" + reservationId;

		paymentCommand = new PaymentCommand(reservationId, queueTokenId.toString());
		
		Payment payment = mock(Payment.class);
		Reservation reservation = mock(Reservation.class);
		Seat seat = mock(Seat.class);
		User user = mock(User.class);
		paymentTransactionResult = new PaymentTransactionResult(payment, reservation, seat, user);

		queueToken = QueueToken.activeTokenOf(queueTokenId, userId, concertId, 10000000L);
	}


	@Test
	@DisplayName("User 락을 획득하지 못하면 트랜잭션을 실행하지 않는다")
	void NotHasUserLock() throws Exception {
		when(queueApplicationService.getQueueToken(queueTokenId.toString())).thenReturn(queueToken);
		when(distributedLockAspect.executeWithLockHasReturn(eq(userLockKey), any()))
			.thenThrow(new CustomException(ErrorCode.LOCK_CONFLICT));

		CustomException exception = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LOCK_CONFLICT);

		verify(queueApplicationService, times(1)).getQueueToken(queueTokenId.toString());
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(userLockKey), any());
		verify(distributedLockAspect, never()).executeWithLockHasReturn(eq(reservationLockKey), any());
		verify(paymentService, never()).processPayment(any(), any());
		verify(eventPublisher, never()).publish(any());
		verify(paymentOutput, never()).ok(any());
	}

	@Test
	@DisplayName("Reservation 락을 획득하지 못하면 트랜잭션을 실행하지 않는다")
	void NotHasReservationLock() throws Exception {
		when(queueApplicationService.getQueueToken(queueTokenId.toString())).thenReturn(queueToken);
		when(distributedLockAspect.executeWithLockHasReturn(eq(userLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<?> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(distributedLockAspect.executeWithLockHasReturn(eq(reservationLockKey), any()))
			.thenThrow(new CustomException(ErrorCode.LOCK_CONFLICT));

		CustomException exception = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LOCK_CONFLICT);

		verify(queueApplicationService, times(1)).getQueueToken(queueTokenId.toString());
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(userLockKey), any());
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(reservationLockKey), any());
		verify(paymentService, never()).processPayment(any(), any());
		verify(eventPublisher, never()).publish(any());
		verify(paymentOutput, never()).ok(any());
	}

	@Test
	@DisplayName("User 락, Reservation 락 획득시 트랜잭션 수행한다")
	void GetUserLockAndReservationLock() throws Exception {
		when(queueApplicationService.getQueueToken(queueTokenId.toString())).thenReturn(queueToken);
		when(distributedLockAspect.executeWithLockHasReturn(eq(userLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<?> userLockCallable = invocation.getArgument(1);
				return userLockCallable.call();
			});

		when(distributedLockAspect.executeWithLockHasReturn(eq(reservationLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<PaymentTransactionResult> reservationLockCallable = invocation.getArgument(1);
				return reservationLockCallable.call();
			});
		when(paymentService.processPayment(paymentCommand, queueToken)).thenReturn(paymentTransactionResult);

		paymentInteractor.payment(paymentCommand);

		verify(queueApplicationService, times(1)).getQueueToken(queueTokenId.toString());
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(userLockKey), any());
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(reservationLockKey), any());
		verify(paymentService, times(1)).processPayment(paymentCommand, queueToken);
		verify(eventPublisher, times(1)).publish(any(PaymentSuccessEvent.class));
		verify(paymentOutput, times(1)).ok(any(PaymentResult.class));
	}

	@Test
	@DisplayName("동시 결제 요청시 하나만 성공한다")
	void ConcurrentPayment() throws Exception {
		int threadCount = 5;
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger lockConflictCount = new AtomicInteger(0);

		when(queueApplicationService.getQueueToken(anyString())).thenReturn(queueToken);
		when(distributedLockAspect.executeWithLockHasReturn(eq(userLockKey), any()))
			.thenAnswer(invocation -> {
				if (successCount.get() == 0) {
					successCount.incrementAndGet();
					Callable<?> callable = invocation.getArgument(1);
					return callable.call();
				} else {
					throw new CustomException(ErrorCode.LOCK_CONFLICT);
				}
			});
		when(distributedLockAspect.executeWithLockHasReturn(eq(reservationLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<PaymentTransactionResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(paymentService.processPayment(any(), any())).thenReturn(paymentTransactionResult);

		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					paymentInteractor.payment(paymentCommand);
				} catch (CustomException e) {
					if (e.getErrorCode() == ErrorCode.LOCK_CONFLICT) {
						lockConflictCount.incrementAndGet();
					}
				} catch (Exception e) {

				} finally {
					latch.countDown();
				}
			});
			futures.add(future);
		}

		latch.await();
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		assertThat(successCount.get()).isEqualTo(1);
		assertThat(lockConflictCount.get()).isEqualTo(threadCount - 1);
	}

}