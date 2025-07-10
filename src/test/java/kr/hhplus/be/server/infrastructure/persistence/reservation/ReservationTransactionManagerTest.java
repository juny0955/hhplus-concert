package kr.hhplus.be.server.infrastructure.persistence.reservation;

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

import kr.hhplus.be.server.api.TestDataFactory;
import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.port.out.PaymentRepository;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.concert.domain.queue.QueueToken;
import kr.hhplus.be.server.concert.port.out.queue.QueueTokenRepository;
import kr.hhplus.be.server.domain.reservation.domain.CreateReservationResult;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.service.ReservationDomainResult;
import kr.hhplus.be.server.reservation.domain.ReservationStatus;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.SeatClass;
import kr.hhplus.be.server.concert.domain.seat.SeatStatus;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.reservation.port.in.reservation.ReserveSeatCommand;

@ExtendWith(MockitoExtension.class)
class ReservationTransactionManagerTest {

	@InjectMocks
	private CreateReservationManager reservationTransactionManager;

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private QueueTokenRepository queueTokenRepository;

	@Mock
	private ConcertRepository concertRepository;

	@Mock
	private ConcertDateRepository concertDateRepository;

	@Mock
	private SeatHoldRepository seatHoldRepository;

	@Mock
	private SeatRepository seatRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private ReservationDomainService reservationDomainService;

	private UUID userId;
	private UUID concertId;
	private UUID concertDateId;
	private UUID seatId;
	private UUID reservationId;
	private UUID paymentId;
	private UUID queueTokenId;
	private String queueTokenIdString;
	private ReserveSeatCommand reserveSeatCommand;
	private QueueToken queueToken;
	private Concert concert;
	private ConcertDate concertDate;
	private Seat seat;
	private Reservation reservation;
	private Payment payment;
	private ReservationDomainResult reservationDomainResult;

	@BeforeEach
	void beforeEach() {
		userId = UUID.randomUUID();
		concertId = UUID.randomUUID();
		concertDateId = UUID.randomUUID();
		seatId = UUID.randomUUID();
		reservationId = UUID.randomUUID();
		paymentId = UUID.randomUUID();
		queueTokenId = UUID.randomUUID();
		queueTokenIdString = queueTokenId.toString();

		LocalDateTime now = LocalDateTime.now();
		reserveSeatCommand = new ReserveSeatCommand(concertId, concertDateId, seatId, queueTokenIdString);
		queueToken = QueueToken.activeTokenOf(queueTokenId, userId, concertId, 1000000);
		concert = TestDataFactory.createConcert();
		concertDate = new ConcertDate(concertDateId, concertId, null, now.plusDays(7), now.plusDays(5), now, now);
		seat = new Seat(seatId, concertDateId, 10, BigDecimal.valueOf(50000), SeatClass.VIP, SeatStatus.AVAILABLE, now, now);
		reservation = new Reservation(reservationId, userId, seatId, ReservationStatus.PENDING, now, now);
		payment = new Payment(paymentId, userId, reservationId, BigDecimal.valueOf(50000), PaymentStatus.PENDING, null, now, now);

		Seat reservedSeat = new Seat(seatId, concertDateId, 10, BigDecimal.valueOf(50000), SeatClass.VIP, SeatStatus.RESERVED, now, now);
		reservationDomainResult = new ReservationDomainResult(reservedSeat, reservation, null);
	}

	@Test
	@DisplayName("예약트랜잭션_성공")
	void processReservationTransaction_Success() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenIdString)).thenReturn(queueToken);
		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(concertDateRepository.findById(concertDateId)).thenReturn(Optional.of(concertDate));
		when(seatRepository.findBySeatIdAndConcertDateId(seatId, concertDateId)).thenReturn(Optional.of(seat));
		when(reservationDomainService.processReservation(concert, concertDate, seat, userId)).thenReturn(reservationDomainResult);
		when(seatRepository.save(any(Seat.class))).thenReturn(seat);
		when(reservationRepository.save(reservation)).thenReturn(reservation);
		when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

		CreateReservationResult result = reservationTransactionManager.processCreateReservation(reserveSeatCommand);

		assertThat(result).isNotNull();
		assertThat(result.reservation()).isEqualTo(reservation);
		assertThat(result.payment()).isEqualTo(payment);
		assertThat(result.seat()).isEqualTo(seat);
		assertThat(result.userId()).isEqualTo(userId);

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenIdString);
		verify(concertRepository, times(1)).existsById(concertId);
		verify(concertDateRepository, times(1)).findById(concertDateId);
		verify(seatRepository, times(1)).findBySeatIdAndConcertDateId(seatId, concertDateId);
		verify(reservationDomainService, times(1)).processReservation(concert, concertDate, seat, userId);
		verify(seatRepository, times(1)).save(any(Seat.class));
		verify(reservationRepository, times(1)).save(reservation);
		verify(paymentRepository, times(1)).save(any(Payment.class));
		verify(seatHoldRepository, times(1)).hold(seatId, userId);
	}

	@Test
	@DisplayName("예약트랜잭션_실패_대기열토큰유효하지않음")
	void processReservationTransaction_Failure_InvalidQueueToken() throws CustomException {
		QueueToken waitingToken = QueueToken.waitingTokenOf(queueTokenId, userId, concertId, 10);
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenIdString)).thenReturn(waitingToken);

		CustomException exception = assertThrows(CustomException.class,
			() -> reservationTransactionManager.processCreateReservation(reserveSeatCommand));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_QUEUE_TOKEN);
		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenIdString);
		verify(concertRepository, never()).existsById(any());
		verify(concertDateRepository, never()).findById(any());
		verify(seatRepository, never()).findBySeatIdAndConcertDateId(any(), any());
		verify(reservationDomainService, never()).processReservation(any(), any(), any(), any());
		verify(seatHoldRepository, never()).hold(any(), any());
	}

	@Test
	@DisplayName("예약트랜잭션_실패_콘서트정보찾지못함")
	void processReservationTransaction_Failure_ConcertNotFound() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenIdString)).thenReturn(queueToken);
		when(concertRepository.existsById(concertId)).thenReturn(false);

		CustomException exception = assertThrows(CustomException.class,
			() -> reservationTransactionManager.processCreateReservation(reserveSeatCommand));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenIdString);
		verify(concertRepository, times(1)).existsById(concertId);
		verify(concertDateRepository, never()).findById(any());
		verify(seatRepository, never()).findBySeatIdAndConcertDateId(any(), any());
		verify(reservationDomainService, never()).processReservation(any(), any(), any(), any());
	}

	@Test
	@DisplayName("예약트랜잭션_실패_콘서트날짜정보찾지못함")
	void processReservationTransaction_Failure_ConcertDateNotFound() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenIdString)).thenReturn(queueToken);
		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(concertDateRepository.findById(concertDateId)).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class,
			() -> reservationTransactionManager.processCreateReservation(reserveSeatCommand));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CONCERT_DATE_NOT_FOUND);
		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenIdString);
		verify(concertRepository, times(1)).existsById(concertId);
		verify(concertDateRepository, times(1)).findById(concertDateId);
		verify(seatRepository, never()).findBySeatIdAndConcertDateId(any(), any());
		verify(reservationDomainService, never()).processReservation(any(), any(), any(), any());
	}

	@Test
	@DisplayName("예약트랜잭션_실패_좌석정보찾지못함")
	void processReservationTransaction_Failure_SeatNotFound() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenIdString)).thenReturn(queueToken);
		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(concertDateRepository.findById(concertDateId)).thenReturn(Optional.of(concertDate));
		when(seatRepository.findBySeatIdAndConcertDateId(seatId, concertDateId)).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class,
			() -> reservationTransactionManager.processCreateReservation(reserveSeatCommand));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SEAT_NOT_FOUND);
		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenIdString);
		verify(concertRepository, times(1)).existsById(concertId);
		verify(concertDateRepository, times(1)).findById(concertDateId);
		verify(seatRepository, times(1)).findBySeatIdAndConcertDateId(seatId, concertDateId);
		verify(reservationDomainService, never()).processReservation(any(), any(), any(), any());
	}

	@Test
	@DisplayName("예약트랜잭션_실패_도메인서비스예외(이미 예약된좌석)")
	void processReservationTransaction_Failure_DomainServiceException_AlreadyReservedSeat() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenIdString)).thenReturn(queueToken);
		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(concertDateRepository.findById(concertDateId)).thenReturn(Optional.of(concertDate));
		when(seatRepository.findBySeatIdAndConcertDateId(seatId, concertDateId)).thenReturn(Optional.of(seat));
		when(reservationDomainService.processReservation(concert, concertDate, seat, userId)).thenThrow(new CustomException(ErrorCode.ALREADY_RESERVED_SEAT));

		CustomException exception = assertThrows(CustomException.class,
			() -> reservationTransactionManager.processCreateReservation(reserveSeatCommand));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_RESERVED_SEAT);
		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenIdString);
		verify(concertRepository, times(1)).existsById(concertId);
		verify(concertDateRepository, times(1)).findById(concertDateId);
		verify(seatRepository, times(1)).findBySeatIdAndConcertDateId(seatId, concertDateId);
		verify(reservationDomainService, times(1)).processReservation(concert, concertDate, seat, userId);
		verify(seatRepository, never()).save(any());
		verify(reservationRepository, never()).save(any());
		verify(paymentRepository, never()).save(any());
		verify(seatHoldRepository, never()).hold(any(), any());
	}

	@Test
	@DisplayName("예약트랜잭션_실패_도메인서비스예외(예약 데드라인지남)")
	void processReservationTransaction_Failure_DomainServiceException_OverDeadline() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenIdString)).thenReturn(queueToken);
		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(concertDateRepository.findById(concertDateId)).thenReturn(Optional.of(concertDate));
		when(seatRepository.findBySeatIdAndConcertDateId(seatId, concertDateId)).thenReturn(Optional.of(seat));
		when(reservationDomainService.processReservation(concert, concertDate, seat, userId)).thenThrow(new CustomException(ErrorCode.OVER_DEADLINE));

		CustomException exception = assertThrows(CustomException.class,
			() -> reservationTransactionManager.processCreateReservation(reserveSeatCommand));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.OVER_DEADLINE);
		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenIdString);
		verify(concertRepository, times(1)).existsById(concertId);
		verify(concertDateRepository, times(1)).findById(concertDateId);
		verify(seatRepository, times(1)).findBySeatIdAndConcertDateId(seatId, concertDateId);
		verify(reservationDomainService, times(1)).processReservation(concert, concertDate, seat, userId);
		verify(seatRepository, never()).save(any());
		verify(reservationRepository, never()).save(any());
		verify(paymentRepository, never()).save(any());
		verify(seatHoldRepository, never()).hold(any(), any());
	}

	@Test
	@DisplayName("예약트랜잭션_실패_좌석이미예약됨")
	void processReservationTransaction_Failure_AlreadyReservedSeat() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(queueTokenIdString)).thenReturn(queueToken);
		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(concertDateRepository.findById(concertDateId)).thenReturn(Optional.of(concertDate));
		when(seatRepository.findBySeatIdAndConcertDateId(seatId, concertDateId)).thenReturn(Optional.of(seat));
		when(reservationDomainService.processReservation(concert, concertDate, seat, userId))
			.thenThrow(new CustomException(ErrorCode.ALREADY_RESERVED_SEAT));

		CustomException exception = assertThrows(CustomException.class,
			() -> reservationTransactionManager.processCreateReservation(reserveSeatCommand));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_RESERVED_SEAT);
		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(queueTokenIdString);
		verify(concertRepository, times(1)).existsById(concertId);
		verify(concertDateRepository, times(1)).findById(concertDateId);
		verify(seatRepository, times(1)).findBySeatIdAndConcertDateId(seatId, concertDateId);
		verify(reservationDomainService, times(1)).processReservation(concert, concertDate, seat, userId);
		verify(seatRepository, never()).save(any(Seat.class));
		verify(reservationRepository, never()).save(any());
		verify(paymentRepository, never()).save(any());
		verify(seatHoldRepository, never()).hold(any(), any());
	}
}