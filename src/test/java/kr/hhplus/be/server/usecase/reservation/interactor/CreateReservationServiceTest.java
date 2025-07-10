package kr.hhplus.be.server.usecase.reservation.interactor;

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
import org.springframework.context.ApplicationEventPublisher;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.SeatClass;
import kr.hhplus.be.server.concert.domain.seat.SeatStatus;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.aop.DistributedLockAspect;
import kr.hhplus.be.server.domain.reservation.domain.CreateReservationResult;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.reservation.domain.reservation.Reservation;
import kr.hhplus.be.server.reservation.domain.reservation.ReservationCreatedEvent;
import kr.hhplus.be.server.reservation.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.reservation.port.in.ReserveSeatCommand;
import kr.hhplus.be.server.reservation.usecase.output.ReservationOutput;

@ExtendWith(MockitoExtension.class)
class CreateReservationServiceTest {

	@InjectMocks
	private CreateReservationService createReservationService;

	@Mock
	private CreateReservationManager createReservationManager;

	@Mock
	private ReservationOutput reservationOutput;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private DistributedLockAspect distributedLockAspect;

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
	void createReservation_Success() throws Exception {
		String expectedLockKey = "concert:" + seatId;

		// 분산락 Mock 설정
		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<CreateReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(createReservationManager.processCreateReservation(reserveSeatCommand))
			.thenReturn(createReservationResult);

		createReservationService.createReservation(reserveSeatCommand);

		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(createReservationManager, times(1)).processCreateReservation(reserveSeatCommand);
		verify(eventPublisher, times(1)).publishEvent(any(ReservationCreatedEvent.class));
		verify(reservationOutput, times(1)).ok(any(ReserveSeatResult.class));
	}

	@Test
	@DisplayName("예약_실패_CustomException")
	void createReservation_Failure_CustomException() throws Exception {
		String expectedLockKey = "concert:" + seatId;
		CustomException expectedException = new CustomException(ErrorCode.SEAT_NOT_FOUND);

		// 분산락 Mock 설정 - 내부에서 예외 발생
		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<CreateReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(createReservationManager.processCreateReservation(reserveSeatCommand))
			.thenThrow(expectedException);

		CustomException customException = assertThrows(CustomException.class,
			() -> createReservationService.createReservation(reserveSeatCommand));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.SEAT_NOT_FOUND);
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(createReservationManager, times(1)).processCreateReservation(reserveSeatCommand);
		verify(eventPublisher, never()).publishEvent(any());
		verify(reservationOutput, never()).ok(any());
	}

	@Test
	@DisplayName("예약_실패_좌석이미예약됨")
	void createReservation() throws Exception {
		String expectedLockKey = "concert:" + seatId;

		// 분산락 Mock 설정 - 내부에서 예외 발생
		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				@SuppressWarnings("unchecked")
				Callable<CreateReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(createReservationManager.processCreateReservation(reserveSeatCommand))
			.thenThrow(new CustomException(ErrorCode.ALREADY_RESERVED_SEAT));

		CustomException customException = assertThrows(CustomException.class,
			() -> createReservationService.createReservation(reserveSeatCommand));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_RESERVED_SEAT);
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(createReservationManager, times(1)).processCreateReservation(reserveSeatCommand);
		verify(eventPublisher, never()).publishEvent(any());
		verify(reservationOutput, never()).ok(any());
	}

	@Test
	@DisplayName("예약_실패_대기열토큰유효하지않음")
	void createReservation_Failure_InvalidQueueToken() throws Exception {
		String expectedLockKey = "concert:" + seatId;

		// 분산락 Mock 설정 - 내부에서 예외 발생
		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<CreateReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(createReservationManager.processCreateReservation(reserveSeatCommand))
			.thenThrow(new CustomException(ErrorCode.INVALID_QUEUE_TOKEN));

		CustomException actualException = assertThrows(CustomException.class,
			() -> createReservationService.createReservation(reserveSeatCommand));

		assertThat(actualException.getErrorCode()).isEqualTo(ErrorCode.INVALID_QUEUE_TOKEN);
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(createReservationManager, times(1)).processCreateReservation(reserveSeatCommand);
		verify(eventPublisher, never()).publishEvent(any());
		verify(reservationOutput, never()).ok(any());
	}

	@Test
	@DisplayName("예약_실패_콘서트정보찾지못함")
	void createReservation_Failure_ConcertNotFound() throws Exception {
		String expectedLockKey = "concert:" + seatId;

		// 분산락 Mock 설정 - 내부에서 예외 발생
		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<CreateReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(createReservationManager.processCreateReservation(reserveSeatCommand))
			.thenThrow(new CustomException(ErrorCode.CONCERT_NOT_FOUND));

		CustomException actualException = assertThrows(CustomException.class,
			() -> createReservationService.createReservation(reserveSeatCommand));

		assertThat(actualException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(createReservationManager, times(1)).processCreateReservation(reserveSeatCommand);
		verify(eventPublisher, never()).publishEvent(any());
		verify(reservationOutput, never()).ok(any());
	}
}