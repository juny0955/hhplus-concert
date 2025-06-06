package kr.hhplus.be.server.usecase.payment.interactor;

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

import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.concert.SeatClass;
import kr.hhplus.be.server.domain.concert.SeatStatus;
import kr.hhplus.be.server.domain.event.KafkaEventObject;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.usecase.concert.SeatRepository;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;
import kr.hhplus.be.server.usecase.payment.PaymentRepository;
import kr.hhplus.be.server.usecase.payment.input.PaymentCommand;
import kr.hhplus.be.server.usecase.payment.output.PaymentOutput;
import kr.hhplus.be.server.usecase.payment.output.PaymentResult;
import kr.hhplus.be.server.usecase.queue.QueueTokenRepository;
import kr.hhplus.be.server.usecase.reservation.ReservationRepository;
import kr.hhplus.be.server.usecase.reservation.SeatHoldRepository;
import kr.hhplus.be.server.usecase.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class PaymentInteractorTest {

	@InjectMocks
	private PaymentInteractor paymentInteractor;

	@Mock
	private QueueTokenRepository queueTokenRepository;

	@Mock
	private SeatHoldRepository seatHoldRepository;

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private SeatRepository seatRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PaymentOutput paymentOutput;

	@Mock
	private EventPublisher eventPublisher;

	private UUID reservationId;
	private UUID queueTokenId;
	private UUID userId;
	private UUID concertId;
	private UUID seatId;
	private UUID paymentId;
	private UUID concertDateId;
	private PaymentCommand paymentCommand;
	private QueueToken queueToken;
	private Reservation reservation;
	private User user;
	private Payment payment;
	private Seat seat;

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
		queueToken = QueueToken.activeTokenOf(queueTokenId, userId, concertId, 1000000);
		reservation = new Reservation(reservationId, userId, seatId, ReservationStatus.PENDING, now, now);
		user = new User(userId, BigDecimal.valueOf(100000), now, now);
		payment = new Payment(paymentId, userId, reservationId, BigDecimal.valueOf(10000), PaymentStatus.PENDING, null, now, now);
		seat = new Seat(seatId, concertDateId, 10, BigDecimal.valueOf(10000), SeatClass.VIP, SeatStatus.RESERVED, now, now);
	}

	@Test
	@DisplayName("결제_성공")
	void payment_Success() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(paymentCommand.queueTokenId())).thenReturn(queueToken);
		when(reservationRepository.findById(paymentCommand.reservationId())).thenReturn(Optional.of(reservation));
		when(paymentRepository.findByReservationId(reservation.id())).thenReturn(Optional.of(payment));
		when(userRepository.findById(queueToken.userId())).thenReturn(Optional.of(user));
		when(seatRepository.findById(reservation.seatId())).thenReturn(Optional.of(seat));
		when(seatHoldRepository.isHoldSeat(seat.id(), user.id())).thenReturn(true);

		paymentInteractor.payment(paymentCommand);

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(paymentCommand.queueTokenId());
		verify(reservationRepository, times(1)).findById(paymentCommand.reservationId());
		verify(paymentRepository, times(1)).findByReservationId(reservation.id());
		verify(seatRepository, times(1)).findById(reservation.seatId());
		verify(userRepository, times(1)).findById(queueToken.userId());
		verify(seatHoldRepository, times(1)).isHoldSeat(seat.id(), user.id());
		verify(userRepository, times(1)).save(any(User.class));
		verify(paymentRepository, times(1)).save(any(Payment.class));
		verify(reservationRepository, times(1)).save(any(Reservation.class));
		verify(seatRepository, times(1)).save(any(Seat.class));
		verify(queueTokenRepository, times(1)).expiresQueueToken(queueToken.tokenId().toString());
		verify(eventPublisher, times(1)).publish(any(KafkaEventObject.class));
		verify(paymentOutput, times(1)).ok(any(PaymentResult.class));
	}

	@Test
	@DisplayName("결제_실패_대기열토큰유효하지않음")
	void payment_Failure_InvalidQueueToken() throws CustomException {
		QueueToken waitingToken = QueueToken.waitingTokenOf(queueTokenId, userId, concertId, 10);

		when(queueTokenRepository.findQueueTokenByTokenId(paymentCommand.queueTokenId())).thenReturn(waitingToken);

		CustomException customException = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(paymentCommand.queueTokenId());
		verify(reservationRepository, never()).findById(paymentCommand.reservationId());
		verify(paymentRepository, never()).findByReservationId(reservation.id());
		verify(seatRepository, never()).findById(reservation.seatId());
		verify(userRepository, never()).findById(queueToken.userId());
		verify(seatHoldRepository, never()).isHoldSeat(seat.id(), user.id());
		verify(userRepository, never()).save(any(User.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(seatRepository, never()).save(any(Seat.class));
		verify(queueTokenRepository, never()).expiresQueueToken(queueToken.tokenId().toString());
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(paymentOutput, never()).ok(any(PaymentResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_QUEUE_TOKEN);
	}

	@Test
	@DisplayName("결제_실패_예약정보찾지못함")
	void payment_Failure_ReservationNotFound() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(paymentCommand.queueTokenId())).thenReturn(queueToken);
		when(reservationRepository.findById(paymentCommand.reservationId())).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(paymentCommand.queueTokenId());
		verify(reservationRepository, times(1)).findById(paymentCommand.reservationId());
		verify(paymentRepository, never()).findByReservationId(reservation.id());
		verify(seatRepository, never()).findById(reservation.seatId());
		verify(userRepository, never()).findById(queueToken.userId());
		verify(seatHoldRepository, never()).isHoldSeat(seat.id(), user.id());
		verify(userRepository, never()).save(any(User.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(seatRepository, never()).save(any(Seat.class));
		verify(queueTokenRepository, never()).expiresQueueToken(queueToken.tokenId().toString());
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(paymentOutput, never()).ok(any(PaymentResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.RESERVATION_NOT_FOUND);
	}

	@Test
	@DisplayName("결제_실패_결제정보찾지못함")
	void payment_Failure_PaymentNotFound() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(paymentCommand.queueTokenId())).thenReturn(queueToken);
		when(reservationRepository.findById(paymentCommand.reservationId())).thenReturn(Optional.of(reservation));
		when(paymentRepository.findByReservationId(reservation.id())).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(paymentCommand.queueTokenId());
		verify(reservationRepository, times(1)).findById(paymentCommand.reservationId());
		verify(paymentRepository, times(1)).findByReservationId(reservation.id());
		verify(seatRepository, never()).findById(reservation.seatId());
		verify(userRepository, never()).findById(queueToken.userId());
		verify(seatHoldRepository, never()).isHoldSeat(seat.id(), user.id());
		verify(userRepository, never()).save(any(User.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(seatRepository, never()).save(any(Seat.class));
		verify(queueTokenRepository, never()).expiresQueueToken(queueToken.tokenId().toString());
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(paymentOutput, never()).ok(any(PaymentResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_NOT_FOUND);
	}

	@Test
	@DisplayName("결제_실패_좌석정보찾지못함")
	void payment_Failure_SeatNotFound() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(paymentCommand.queueTokenId())).thenReturn(queueToken);
		when(reservationRepository.findById(paymentCommand.reservationId())).thenReturn(Optional.of(reservation));
		when(paymentRepository.findByReservationId(reservation.id())).thenReturn(Optional.of(payment));
		when(seatRepository.findById(reservation.seatId())).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(paymentCommand.queueTokenId());
		verify(reservationRepository, times(1)).findById(paymentCommand.reservationId());
		verify(paymentRepository, times(1)).findByReservationId(reservation.id());
		verify(seatRepository, times(1)).findById(reservation.seatId());
		verify(userRepository, never()).findById(queueToken.userId());
		verify(seatHoldRepository, never()).isHoldSeat(seat.id(), user.id());
		verify(userRepository, never()).save(any(User.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(seatRepository, never()).save(any(Seat.class));
		verify(queueTokenRepository, never()).expiresQueueToken(queueToken.tokenId().toString());
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(paymentOutput, never()).ok(any(PaymentResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.SEAT_NOT_FOUND);
	}

	@Test
	@DisplayName("결제_실패_유저정보찾지못함")
	void payment_Failure_UserNotFound() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(paymentCommand.queueTokenId())).thenReturn(queueToken);
		when(reservationRepository.findById(paymentCommand.reservationId())).thenReturn(Optional.of(reservation));
		when(paymentRepository.findByReservationId(reservation.id())).thenReturn(Optional.of(payment));
		when(seatRepository.findById(reservation.seatId())).thenReturn(Optional.of(seat));
		when(userRepository.findById(queueToken.userId())).thenReturn(Optional.empty());

		CustomException customException = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(paymentCommand.queueTokenId());
		verify(reservationRepository, times(1)).findById(paymentCommand.reservationId());
		verify(paymentRepository, times(1)).findByReservationId(reservation.id());
		verify(seatRepository, times(1)).findById(reservation.seatId());
		verify(userRepository, times(1)).findById(queueToken.userId());
		verify(seatHoldRepository, never()).isHoldSeat(seat.id(), user.id());
		verify(userRepository, never()).save(any(User.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(seatRepository, never()).save(any(Seat.class));
		verify(queueTokenRepository, never()).expiresQueueToken(queueToken.tokenId().toString());
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(paymentOutput, never()).ok(any(PaymentResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("결제_실패_유저잔액부족")
	void payment_Failure_UserNotEnoughPoint() throws CustomException {
		User poorUser = new User(userId, BigDecimal.valueOf(5000), LocalDateTime.now(), LocalDateTime.now());

		when(queueTokenRepository.findQueueTokenByTokenId(paymentCommand.queueTokenId())).thenReturn(queueToken);
		when(reservationRepository.findById(paymentCommand.reservationId())).thenReturn(Optional.of(reservation));
		when(paymentRepository.findByReservationId(reservation.id())).thenReturn(Optional.of(payment));
		when(seatRepository.findById(reservation.seatId())).thenReturn(Optional.of(seat));
		when(userRepository.findById(queueToken.userId())).thenReturn(Optional.of(poorUser));

		CustomException customException = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(paymentCommand.queueTokenId());
		verify(reservationRepository, times(1)).findById(paymentCommand.reservationId());
		verify(paymentRepository, times(1)).findByReservationId(reservation.id());
		verify(seatRepository, times(1)).findById(reservation.seatId());
		verify(userRepository, times(1)).findById(queueToken.userId());
		verify(seatHoldRepository, never()).isHoldSeat(seat.id(), user.id());
		verify(userRepository, never()).save(any(User.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(seatRepository, never()).save(any(Seat.class));
		verify(queueTokenRepository, never()).expiresQueueToken(queueToken.tokenId().toString());
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(paymentOutput, never()).ok(any(PaymentResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_BALANCE);
	}

	@Test
	@DisplayName("결제_실패_임시배정끝남")
	void payment_Failure_ExpiredSeatHold() throws CustomException {
		when(queueTokenRepository.findQueueTokenByTokenId(paymentCommand.queueTokenId())).thenReturn(queueToken);
		when(reservationRepository.findById(paymentCommand.reservationId())).thenReturn(Optional.of(reservation));
		when(paymentRepository.findByReservationId(reservation.id())).thenReturn(Optional.of(payment));
		when(seatRepository.findById(reservation.seatId())).thenReturn(Optional.of(seat));
		when(userRepository.findById(queueToken.userId())).thenReturn(Optional.of(user));
		when(seatHoldRepository.isHoldSeat(seat.id(), user.id())).thenReturn(false);

		CustomException customException = assertThrows(CustomException.class,
			() -> paymentInteractor.payment(paymentCommand));

		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(paymentCommand.queueTokenId());
		verify(reservationRepository, times(1)).findById(paymentCommand.reservationId());
		verify(paymentRepository, times(1)).findByReservationId(reservation.id());
		verify(seatRepository, times(1)).findById(reservation.seatId());
		verify(userRepository, times(1)).findById(queueToken.userId());
		verify(seatHoldRepository, times(1)).isHoldSeat(seat.id(), user.id());
		verify(userRepository, never()).save(any(User.class));
		verify(paymentRepository, never()).save(any(Payment.class));
		verify(reservationRepository, never()).save(any(Reservation.class));
		verify(seatRepository, never()).save(any(Seat.class));
		verify(queueTokenRepository, never()).expiresQueueToken(queueToken.tokenId().toString());
		verify(eventPublisher, never()).publish(any(KafkaEventObject.class));
		verify(paymentOutput, never()).ok(any(PaymentResult.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.SEAT_NOT_HOLD);
	}
}