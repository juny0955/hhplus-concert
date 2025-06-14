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

import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatClass;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.domain.event.reservation.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationDomainResult;
import kr.hhplus.be.server.domain.reservation.ReservationDomainService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.concertDate.ConcertDateRepository;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.queue.QueueTokenRepository;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.seat.SeatHoldRepository;
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
	private ReservationOutput reservationOutput;

	@Mock
	private ReservationDomainService reservationDomainService;

	@Mock
	private EventPublisher eventPublisher;

	private UUID concertId;
	private UUID concertDateId;
	private UUID seatId;
	private UUID userId;
	private UUID queueTokenId;
	private UUID reservationId;
	private UUID paymentId;
	private ReserveSeatCommand command;
	private QueueToken queueToken;
	private Seat seat;
	private Seat reservedSeat;
	private ConcertDate concertDate;
	private Reservation reservation;
	private Payment payment;
	private ReservationDomainResult domainResult;

	@BeforeEach
	void beforeEach() {
		LocalDateTime now = LocalDateTime.now();
		concertId = UUID.randomUUID();
		concertDateId = UUID.randomUUID();
		seatId = UUID.randomUUID();
		userId = UUID.randomUUID();
		queueTokenId = UUID.randomUUID();
		reservationId = UUID.randomUUID();
		paymentId = UUID.randomUUID();

		command = new ReserveSeatCommand(concertId, concertDateId, seatId, queueTokenId.toString());
		queueToken = QueueToken.activeTokenOf(queueTokenId, userId, concertId, 1000000);
		seat = new Seat(seatId, concertDateId, 10, BigDecimal.valueOf(10000), SeatClass.VIP, SeatStatus.AVAILABLE, now, now);
		reservedSeat = seat.reserve();
		concertDate = new ConcertDate(concertDateId, concertId, null, now.plusDays(7), now.plusDays(5), now, now);
		reservation = new Reservation(reservationId, userId, seatId, ReservationStatus.PENDING, now, now);
		payment = new Payment(paymentId, userId, reservationId, BigDecimal.valueOf(10000), PaymentStatus.PENDING, null, now, now);
		domainResult = new ReservationDomainResult(reservedSeat, reservation);
	}

	@Test
	@DisplayName("콘서트_좌석_예약_성공")
	void concertSeatReservation_Success() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId.toString())).thenReturn(queueToken);
		when(concertRepository.existsById(command.concertId())).thenReturn(true);
		when(concertDateRepository.findById(command.concertDateId())).thenReturn(Optional.of(concertDate));
		when(seatRepository.findBySeatIdAndConcertDateId(command.seatId(), command.concertDateId())).thenReturn(Optional.of(seat));
		when(reservationDomainService.processReservation(concertDate, seat, userId)).thenReturn(domainResult);
		when(seatRepository.save(reservedSeat)).thenReturn(reservedSeat);
		when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
		when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

		// When
		reservationInteractor.reserveSeat(command);

		// Then
		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId.toString());
		verify(concertRepository, times(1)).existsById(command.concertId());
		verify(concertDateRepository, times(1)).findById(command.concertDateId());
		verify(seatRepository, times(1)).findBySeatIdAndConcertDateId(command.seatId(), command.concertDateId());
		verify(reservationDomainService, times(1)).processReservation(concertDate, seat, userId);
		verify(seatRepository, times(1)).save(reservedSeat);
		verify(reservationRepository, times(1)).save(any(Reservation.class));
		verify(paymentRepository, times(1)).save(any(Payment.class));
		verify(seatHoldRepository, times(1)).hold(seatId, userId);
		verify(eventPublisher, times(1)).publish(any(ReservationCreatedEvent.class));
		verify(reservationOutput, times(1)).ok(any(ReserveSeatResult.class));
	}

	@Test
	@DisplayName("콘서트_좌석_예약_실패_대기열토큰유효하지않음")
	void concertSeatReservation_Failure_InvalidQueueToken() throws CustomException {
		QueueToken waitingToken = QueueToken.waitingTokenOf(queueTokenId, userId, concertId, 10);
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId.toString())).thenReturn(waitingToken);

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationInteractor.reserveSeat(command));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId.toString());
		verify(concertRepository, never()).existsById(any());
		verify(concertDateRepository, never()).findById(any());
		verify(seatRepository, never()).findBySeatIdAndConcertDateId(any(), any());
		verify(reservationDomainService, never()).processReservation(any(), any(), any());
		verify(seatRepository, never()).save(any());
		verify(reservationRepository, never()).save(any());
		verify(paymentRepository, never()).save(any());
		verify(seatHoldRepository, never()).hold(any(), any());
		verify(eventPublisher, never()).publish(any(ReservationCreatedEvent.class));
		verify(reservationOutput, never()).ok(any());

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_QUEUE_TOKEN);
	}

	@Test
	@DisplayName("콘서트_좌석_예약_실패_콘서트못찾음")
	void concertSeatReservation_Failure_ConcertNotFound() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId.toString())).thenReturn(queueToken);
		when(concertRepository.existsById(command.concertId())).thenReturn(false);

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationInteractor.reserveSeat(command));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId.toString());
		verify(concertRepository, times(1)).existsById(command.concertId());
		verify(concertDateRepository, never()).findById(any());
		verify(seatRepository, never()).findBySeatIdAndConcertDateId(any(), any());
		verify(reservationDomainService, never()).processReservation(any(), any(), any());
		verify(seatRepository, never()).save(any());
		verify(reservationRepository, never()).save(any());
		verify(paymentRepository, never()).save(any());
		verify(seatHoldRepository, never()).hold(any(), any());
		verify(eventPublisher, never()).publish(any(ReservationCreatedEvent.class));
		verify(reservationOutput, never()).ok(any());

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
	}

	@Test
	@DisplayName("콘서트_좌석_예약_실패_콘서트날짜못찾음")
	void concertSeatReservation_Failure_ConcertDateNotFound() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId.toString())).thenReturn(queueToken);
		when(concertRepository.existsById(command.concertId())).thenReturn(true);
		when(concertDateRepository.findById(command.concertDateId())).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationInteractor.reserveSeat(command));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId.toString());
		verify(concertRepository, times(1)).existsById(command.concertId());
		verify(concertDateRepository, times(1)).findById(command.concertDateId());
		verify(seatRepository, never()).findBySeatIdAndConcertDateId(any(), any());
		verify(reservationDomainService, never()).processReservation(any(), any(), any());
		verify(seatRepository, never()).save(any());
		verify(reservationRepository, never()).save(any());
		verify(paymentRepository, never()).save(any());
		verify(seatHoldRepository, never()).hold(any(), any());
		verify(eventPublisher, never()).publish(any(ReservationCreatedEvent.class));
		verify(reservationOutput, never()).ok(any());

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_DATE_NOT_FOUND);
	}

	@Test
	@DisplayName("콘서트_좌석_예약_실패_좌석못찾음")
	void concertSeatReservation_Failure_SeatNotFound() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenId.toString())).thenReturn(queueToken);
		when(concertRepository.existsById(command.concertId())).thenReturn(true);
		when(concertDateRepository.findById(command.concertDateId())).thenReturn(Optional.of(concertDate));
		when(seatRepository.findBySeatIdAndConcertDateId(command.seatId(), command.concertDateId())).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> reservationInteractor.reserveSeat(command));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenId.toString());
		verify(concertRepository, times(1)).existsById(command.concertId());
		verify(concertDateRepository, times(1)).findById(command.concertDateId());
		verify(seatRepository, times(1)).findBySeatIdAndConcertDateId(command.seatId(), command.concertDateId());
		verify(reservationDomainService, never()).processReservation(any(), any(), any());
		verify(seatRepository, never()).save(any());
		verify(reservationRepository, never()).save(any());
		verify(paymentRepository, never()).save(any());
		verify(seatHoldRepository, never()).hold(any(), any());
		verify(eventPublisher, never()).publish(any(ReservationCreatedEvent.class));
		verify(reservationOutput, never()).ok(any());

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.SEAT_NOT_FOUND);
	}
}