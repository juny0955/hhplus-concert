package kr.hhplus.be.server.usecase.reservation.interactor;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.concert.ConcertDate;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.concert.SeatClass;
import kr.hhplus.be.server.domain.concert.SeatStatus;
import kr.hhplus.be.server.domain.event.KafkaEventObject;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.usecase.concert.ConcertDateRepository;
import kr.hhplus.be.server.usecase.concert.ConcertRepository;
import kr.hhplus.be.server.usecase.concert.SeatRepository;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;
import kr.hhplus.be.server.usecase.payment.PaymentRepository;
import kr.hhplus.be.server.usecase.queue.QueueTokenRepository;
import kr.hhplus.be.server.usecase.reservation.ReservationRepository;
import kr.hhplus.be.server.usecase.reservation.SeatHoldRepository;
import kr.hhplus.be.server.usecase.reservation.SeatLockRepository;
import kr.hhplus.be.server.usecase.reservation.input.ReserveSeatCommand;
import kr.hhplus.be.server.usecase.reservation.output.ReservationOutput;
import kr.hhplus.be.server.usecase.reservation.output.ReserveSeatResult;

@ExtendWith(MockitoExtension.class)
class ReservationInteractorTest {

	@InjectMocks
	private ReservationInteractor reservationInteractor;

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private QueueTokenRepository queueTokenRepository;

	@Mock
	private SeatLockRepository seatLockRepository;

	@Mock
	private SeatRepository seatRepository;

	@Mock
	private ConcertDateRepository concertDateRepository;

	@Mock
	private ConcertRepository concertRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private SeatHoldRepository seatHoldRepository;

	@Mock
	private EventPublisher eventPublisher;

	@Mock
	private ReservationOutput reservationOutput;

	private UUID concertId;
	private UUID concertDateId;
	private UUID seatId;
	private UUID userId;
	private String queueTokenId;
	private ReserveSeatCommand command;
	private QueueToken queueToken;
	private Seat seat;
	private ConcertDate concertDate;

	@BeforeEach
	void beforeEach() {
		LocalDateTime now = LocalDateTime.now();
		concertId = UUID.randomUUID();
		seatId = UUID.randomUUID();
		userId = UUID.randomUUID();
		queueTokenId = "test-queue-token";
		command = new ReserveSeatCommand(concertId, seatId, concertDateId, queueTokenId);
		queueToken = QueueToken.activeTokenOf(UUID.fromString(queueTokenId), userId, concertId, 1000000);
		seat = new Seat(seatId, 10, BigDecimal.valueOf(10000), SeatClass.VIP, SeatStatus.AVAILABLE, now, now);
		concertDate = new ConcertDate(concertDateId, concertId, null, now.plusDays(7), now.plusDays(5), now, now);
	}

	@Test
	@DisplayName("콘서트_좌석_예약_성공")
	void concertSeatReservation_Success() {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId)).thenReturn(queueToken);
		when(concertRepository.existsConcert(command.concertId())).thenReturn(true);
		when(concertDateRepository.findById(command.concertDateId())).thenReturn(Optional.of(concertDate));
		when(seatRepository.findById(command.seatId())).thenReturn(Optional.of(seat));
		when(seatLockRepository.getLock(command.seatId())).thenReturn(true);

		reservationInteractor.reserveSeat(command);

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId);
		verify(concertRepository, times(1)).existsConcert(command.concertId());
		verify(concertDateRepository, times(1)).findById(command.concertDateId());
		verify(seatRepository, times(1)).findById(command.seatId());
		verify(seatLockRepository, times(1)).getLock(command.seatId());
		verify(seatRepository, times(1)).save(any(Seat.class));
		verify(reservationRepository, times(1)).save(any(Reservation.class));
		verify(paymentRepository, times(1)).save(any(Payment.class));
		verify(seatHoldRepository, times(1)).hold(seatId);
		verify(seatLockRepository, times(1)).releaseLock(seatId);
		verify(eventPublisher, times(1)).publish(any(KafkaEventObject.class));
		verify(reservationOutput, times(1)).ok(any(ReserveSeatResult.class));
	}

	@Test
	@DisplayName("콘서트_좌석_예약_실패_대기열토큰유효하지않음")
	void concertSeatReservation_Failure_InvalidQueueToken() {
		QueueToken waitingToken = QueueToken.waitingTokenOf(UUID.fromString(queueTokenId), userId, concertId, 10);

		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId)).thenReturn(waitingToken);

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationInteractor.reserveSeat(command));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId);
		verify(concertRepository, never()).existsConcert(command.concertId());
		verify(concertDateRepository, never()).findById(command.concertDateId());
		verify(seatRepository, never()).findById(command.seatId());
		verify(seatLockRepository, never()).getLock(command.seatId());
		verify(seatRepository, never()).save(any(Seat.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(seatHoldRepository, never()).hold(seatId);
		verify(seatLockRepository, never()).releaseLock(seatId);
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(reservationOutput, never()).ok(any(ReserveSeatResult.class));
		
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_QUEUE_TOKEN);
	}

	@Test
	@DisplayName("콘서트_좌석_예약_실패_콘서트못찾음")
	void concertSeatReservation_Failure_ConcertNotFound() {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId)).thenReturn(queueToken);
		when(concertRepository.existsConcert(command.concertId())).thenReturn(false);

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationInteractor.reserveSeat(command));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId);
		verify(concertRepository, times(1)).existsConcert(command.concertId());
		verify(concertDateRepository, never()).findById(command.concertDateId());
		verify(seatRepository, never()).findById(command.seatId());
		verify(seatLockRepository, never()).getLock(command.seatId());
		verify(seatRepository, never()).save(any(Seat.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(seatHoldRepository, never()).hold(seatId);
		verify(seatLockRepository, never()).releaseLock(seatId);
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(reservationOutput, never()).ok(any(ReserveSeatResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
	}

	@Test
	@DisplayName("콘서트_좌석_예약_실패_콘서트날짜못찾음")
	void concertSeatReservation_Failure_ConcertDateNotFound() {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId)).thenReturn(queueToken);
		when(concertRepository.existsConcert(command.concertId())).thenReturn(true);
		when(concertDateRepository.findById(command.concertDateId())).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationInteractor.reserveSeat(command));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId);
		verify(concertRepository, times(1)).existsConcert(command.concertId());
		verify(concertDateRepository, times(1)).findById(command.concertDateId());
		verify(seatRepository, never()).findById(command.seatId());
		verify(seatLockRepository, never()).getLock(command.seatId());
		verify(seatRepository, never()).save(any(Seat.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(seatHoldRepository, never()).hold(seatId);
		verify(seatLockRepository, never()).releaseLock(seatId);
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(reservationOutput, never()).ok(any(ReserveSeatResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_DATE_NOT_FOUND);
	}

	@Test
	@DisplayName("콘서트_좌석_예약_실패_콘서트날짜데드라인초과")
	void concertSeatReservation_Failure_OverDeadline() {
		ConcertDate concertDate = new ConcertDate(concertDateId, concertId, null, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(3), LocalDateTime.now(), LocalDateTime.now());

		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId)).thenReturn(queueToken);
		when(concertRepository.existsConcert(command.concertId())).thenReturn(true);
		when(concertDateRepository.findById(command.concertDateId())).thenReturn(Optional.of(concertDate));

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationInteractor.reserveSeat(command));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId);
		verify(concertRepository, times(1)).existsConcert(command.concertId());
		verify(concertDateRepository, times(1)).findById(command.concertDateId());
		verify(seatRepository, never()).findById(command.seatId());
		verify(seatLockRepository, never()).getLock(command.seatId());
		verify(seatRepository, never()).save(any(Seat.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(seatHoldRepository, never()).hold(seatId);
		verify(seatLockRepository, never()).releaseLock(seatId);
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(reservationOutput, never()).ok(any(ReserveSeatResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.OVER_DEADLINE);
	}

	@Test
	@DisplayName("콘서트_좌석_예약_실패_좌석못찾음")
	void concertSeatReservation_Failure_SeatNotFound() {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId)).thenReturn(queueToken);
		when(concertRepository.existsConcert(command.concertId())).thenReturn(true);
		when(concertDateRepository.findById(command.concertDateId())).thenReturn(Optional.of(concertDate));
		when(seatRepository.findById(command.seatId())).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationInteractor.reserveSeat(command));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId);
		verify(concertRepository, times(1)).existsConcert(command.concertId());
		verify(concertDateRepository, times(1)).findById(command.concertDateId());
		verify(seatRepository, times(1)).findById(command.seatId());
		verify(seatLockRepository, never()).getLock(command.seatId());
		verify(seatRepository, never()).save(any(Seat.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(seatHoldRepository, never()).hold(seatId);
		verify(seatLockRepository, never()).releaseLock(seatId);
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(reservationOutput, never()).ok(any(ReserveSeatResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.SEAT_NOT_FOUND);
	}

	@Test
	@DisplayName("콘서트_좌석_예약_실패_좌석예약중")
	void concertSeatReservation_Failure_SeatIsReserved() {
		Seat reservationSeat = new Seat(seatId, 10, BigDecimal.valueOf(10000), SeatClass.VIP, SeatStatus.RESERVED, LocalDateTime.now(), LocalDateTime.now());

		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId)).thenReturn(queueToken);
		when(concertRepository.existsConcert(command.concertId())).thenReturn(true);
		when(concertDateRepository.findById(command.concertDateId())).thenReturn(Optional.of(concertDate));
		when(seatRepository.findById(command.seatId())).thenReturn(Optional.of(reservationSeat));

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationInteractor.reserveSeat(command));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId);
		verify(concertRepository, times(1)).existsConcert(command.concertId());
		verify(concertDateRepository, times(1)).findById(command.concertDateId());
		verify(seatRepository, times(1)).findById(command.seatId());
		verify(seatLockRepository, never()).getLock(command.seatId());
		verify(seatRepository, never()).save(any(Seat.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(seatHoldRepository, never()).hold(seatId);
		verify(seatLockRepository, never()).releaseLock(seatId);
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(reservationOutput, never()).ok(any(ReserveSeatResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_RESERVED_SEAT);
	}

	@Test
	@DisplayName("콘서트_좌석_예약_실패_좌석락획득실패")
	void concertSeatReservation_Failure_getSeatLockFail() {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId)).thenReturn(queueToken);
		when(concertRepository.existsConcert(command.concertId())).thenReturn(true);
		when(concertDateRepository.findById(command.concertDateId())).thenReturn(Optional.of(concertDate));
		when(seatRepository.findById(command.seatId())).thenReturn(Optional.of(seat));
		when(seatLockRepository.getLock(command.seatId())).thenReturn(false);

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationInteractor.reserveSeat(command));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId);
		verify(concertRepository, times(1)).existsConcert(command.concertId());
		verify(concertDateRepository, times(1)).findById(command.concertDateId());
		verify(seatRepository, times(1)).findById(command.seatId());
		verify(seatLockRepository, times(1)).getLock(command.seatId());
		verify(seatRepository, never()).save(any(Seat.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(seatHoldRepository, never()).hold(seatId);
		verify(seatLockRepository, never()).releaseLock(seatId);
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(reservationOutput, never()).ok(any(ReserveSeatResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.SEAT_LOCK_CONFLICT);
	}

}