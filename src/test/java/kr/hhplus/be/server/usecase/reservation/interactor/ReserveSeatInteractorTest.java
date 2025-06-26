package kr.hhplus.be.server.usecase.reservation.interactor;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;

import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.infrastructure.persistence.reservation.CreateReservationManager;
import kr.hhplus.be.server.infrastructure.persistence.reservation.CreateReservationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.event.reservation.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatClass;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.reservation.input.ReserveSeatCommand;
import kr.hhplus.be.server.usecase.reservation.output.ReservationOutput;
import kr.hhplus.be.server.usecase.reservation.output.ReserveSeatResult;

@ExtendWith(MockitoExtension.class)
class ReserveSeatInteractorTest {

	@InjectMocks
	private ReserveSeatInteractor reserveSeatInteractor;

	@Mock
	private CreateReservationManager createReservationManager;

	@Mock
	private ReservationOutput reservationOutput;

	@Mock
	private EventPublisher eventPublisher;

	@Mock
	private DistributedLockManager distributedLockManager;

	private UUID userId;
	private UUID concertId;
	private UUID concertDateId;
	private UUID seatId;
	private UUID reservationId;
	private UUID paymentId;
	private String queueTokenId;
	private ReserveSeatCommand reserveSeatCommand;
	private CreateReservationResult createReservationResult;
	private Reservation reservation;
	private Payment payment;
	private Seat seat;

	@BeforeEach
	void beforeEach() {
		userId = UUID.randomUUID();
		concertId = UUID.randomUUID();
		concertDateId = UUID.randomUUID();
		seatId = UUID.randomUUID();
		reservationId = UUID.randomUUID();
		paymentId = UUID.randomUUID();
		queueTokenId = UUID.randomUUID().toString();

		LocalDateTime now = LocalDateTime.now();
		reserveSeatCommand = new ReserveSeatCommand(concertId, concertDateId, seatId, queueTokenId);

		reservation = new Reservation(reservationId, userId, seatId, ReservationStatus.PENDING, now, now);
		payment = new Payment(paymentId, userId, reservationId, BigDecimal.valueOf(50000), PaymentStatus.PENDING, null, now, now);
		seat = new Seat(seatId, concertDateId, 10, BigDecimal.valueOf(50000), SeatClass.VIP, SeatStatus.RESERVED, now, now);

		createReservationResult = new CreateReservationResult(reservation, payment, seat, userId);
	}

	@Test
	@DisplayName("예약_성공")
	void reserveSeat_Success() throws Exception {
		String expectedLockKey = "seat:" + seatId;

		// 분산락 Mock 설정
		when(distributedLockManager.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<CreateReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(createReservationManager.processCreateReservation(reserveSeatCommand))
			.thenReturn(createReservationResult);

		reserveSeatInteractor.reserveSeat(reserveSeatCommand);

		verify(distributedLockManager, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(createReservationManager, times(1)).processCreateReservation(reserveSeatCommand);
		verify(eventPublisher, times(1)).publish(any(ReservationCreatedEvent.class));
		verify(reservationOutput, times(1)).ok(any(ReserveSeatResult.class));
	}

	@Test
	@DisplayName("예약_실패_CustomException")
	void reserveSeat_Failure_CustomException() throws Exception {
		String expectedLockKey = "seat:" + seatId;
		CustomException expectedException = new CustomException(ErrorCode.SEAT_NOT_FOUND);

		// 분산락 Mock 설정 - 내부에서 예외 발생
		when(distributedLockManager.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<CreateReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(createReservationManager.processCreateReservation(reserveSeatCommand))
			.thenThrow(expectedException);

		CustomException customException = assertThrows(CustomException.class,
			() -> reserveSeatInteractor.reserveSeat(reserveSeatCommand));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.SEAT_NOT_FOUND);
		verify(distributedLockManager, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(createReservationManager, times(1)).processCreateReservation(reserveSeatCommand);
		verify(eventPublisher, never()).publish(any());
		verify(reservationOutput, never()).ok(any());
	}

	@Test
	@DisplayName("예약_실패_좌석이미예약됨")
	void reserveSeat_Failure_AlreadyReservedSeat() throws Exception {
		String expectedLockKey = "seat:" + seatId;

		// 분산락 Mock 설정 - 내부에서 예외 발생
		when(distributedLockManager.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				@SuppressWarnings("unchecked")
				Callable<CreateReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(createReservationManager.processCreateReservation(reserveSeatCommand))
			.thenThrow(new CustomException(ErrorCode.ALREADY_RESERVED_SEAT));

		CustomException customException = assertThrows(CustomException.class,
			() -> reserveSeatInteractor.reserveSeat(reserveSeatCommand));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_RESERVED_SEAT);
		verify(distributedLockManager, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(createReservationManager, times(1)).processCreateReservation(reserveSeatCommand);
		verify(eventPublisher, never()).publish(any());
		verify(reservationOutput, never()).ok(any());
	}

	@Test
	@DisplayName("예약_실패_대기열토큰유효하지않음")
	void reserveSeat_Failure_InvalidQueueToken() throws Exception {
		String expectedLockKey = "seat:" + seatId;

		// 분산락 Mock 설정 - 내부에서 예외 발생
		when(distributedLockManager.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<CreateReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(createReservationManager.processCreateReservation(reserveSeatCommand))
			.thenThrow(new CustomException(ErrorCode.INVALID_QUEUE_TOKEN));

		CustomException actualException = assertThrows(CustomException.class,
			() -> reserveSeatInteractor.reserveSeat(reserveSeatCommand));

		assertThat(actualException.getErrorCode()).isEqualTo(ErrorCode.INVALID_QUEUE_TOKEN);
		verify(distributedLockManager, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(createReservationManager, times(1)).processCreateReservation(reserveSeatCommand);
		verify(eventPublisher, never()).publish(any());
		verify(reservationOutput, never()).ok(any());
	}

	@Test
	@DisplayName("예약_실패_콘서트정보찾지못함")
	void reserveSeat_Failure_ConcertNotFound() throws Exception {
		String expectedLockKey = "seat:" + seatId;

		// 분산락 Mock 설정 - 내부에서 예외 발생
		when(distributedLockManager.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<CreateReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(createReservationManager.processCreateReservation(reserveSeatCommand))
			.thenThrow(new CustomException(ErrorCode.CONCERT_NOT_FOUND));

		CustomException actualException = assertThrows(CustomException.class,
			() -> reserveSeatInteractor.reserveSeat(reserveSeatCommand));

		assertThat(actualException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
		verify(distributedLockManager, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(createReservationManager, times(1)).processCreateReservation(reserveSeatCommand);
		verify(eventPublisher, never()).publish(any());
		verify(reservationOutput, never()).ok(any());
	}
}