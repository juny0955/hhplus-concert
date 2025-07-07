package kr.hhplus.be.server.infrastructure.persistence.payment;

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

import kr.hhplus.be.server.payment.application.service.PaymentApplicationService;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentTransactionResult;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainResult;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainService;
import kr.hhplus.be.server.payment.ports.out.PaymentRepository;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.out.QueueTokenRepository;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.ports.out.ReservationRepository;
import kr.hhplus.be.server.reservation.domain.ReservationStatus;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.SeatClass;
import kr.hhplus.be.server.concert.ports.out.SeatHoldRepository;
import kr.hhplus.be.server.concert.ports.out.SeatRepository;
import kr.hhplus.be.server.concert.domain.seat.SeatStatus;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.ports.out.persistence.UserRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.payment.ports.in.PaymentCommand;

@ExtendWith(MockitoExtension.class)
class PaymentApplicationServiceTest {

	@InjectMocks
	private PaymentApplicationService paymentApplicationService;

	@Mock
	private QueueTokenRepository queueTokenRepository;

	@Mock
	private ReservationRepository reservationRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private SeatRepository seatRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private SeatHoldRepository seatHoldRepository;

	@Mock
	private PaymentDomainService paymentDomainService;

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
	private PaymentDomainResult paymentDomainResult;

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

		Payment successPayment = new Payment(paymentId, userId, reservationId, BigDecimal.valueOf(10000), PaymentStatus.SUCCESS, null, now, now);
		Reservation successReservation = new Reservation(reservationId, userId, seatId, ReservationStatus.SUCCESS, now, now);
		Seat successSeat = new Seat(seatId, concertDateId, 10, BigDecimal.valueOf(10000), SeatClass.VIP, SeatStatus.ASSIGNED, now, now);
		User successUser = new User(userId, BigDecimal.valueOf(90000), now, now);

		paymentDomainResult = new PaymentDomainResult(successUser, successReservation, successPayment, successSeat);
	}

	@Test
	@DisplayName("결제트랜잭션_성공")
	void processPayment_Success() throws CustomException {
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
		when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(seatHoldRepository.isHoldSeat(seatId, userId)).thenReturn(true);
		when(paymentRepository.findByReservationId(reservationId)).thenReturn(Optional.of(payment));
		when(paymentDomainService.processPayment(reservation, payment, seat, user)).thenReturn(paymentDomainResult);
		
		when(paymentRepository.save(paymentDomainResult.payment())).thenReturn(paymentDomainResult.payment());
		when(userRepository.save(paymentDomainResult.user())).thenReturn(paymentDomainResult.user());
		when(reservationRepository.save(paymentDomainResult.reservation())).thenReturn(paymentDomainResult.reservation());
		when(seatRepository.save(paymentDomainResult.seat())).thenReturn(paymentDomainResult.seat());

		PaymentTransactionResult result = paymentApplicationService.processPayment(paymentCommand, queueToken);

		assertThat(result).isNotNull();
		assertThat(result.payment()).isEqualTo(paymentDomainResult.payment());
		assertThat(result.reservation()).isEqualTo(paymentDomainResult.reservation());
		assertThat(result.seat()).isEqualTo(paymentDomainResult.seat());
		assertThat(result.user()).isEqualTo(paymentDomainResult.user());

		verify(reservationRepository, times(1)).findById(reservationId);
		verify(seatRepository, times(1)).findById(seatId);
		verify(userRepository, times(1)).findById(userId);
		verify(seatHoldRepository, times(1)).isHoldSeat(seatId, userId);
		verify(paymentRepository, times(1)).findByReservationId(reservationId);
		verify(paymentDomainService, times(1)).processPayment(reservation, payment, seat, user);
		verify(paymentRepository, times(1)).save(paymentDomainResult.payment());
		verify(userRepository, times(1)).save(paymentDomainResult.user());
		verify(reservationRepository, times(1)).save(paymentDomainResult.reservation());
		verify(seatRepository, times(1)).save(paymentDomainResult.seat());
		verify(seatHoldRepository, times(1)).deleteHold(seatId, userId);
		verify(queueTokenRepository, times(1)).expiresQueueToken(queueTokenId.toString());
	}

	@Test
	@DisplayName("결제트랜잭션_실패_예약정보찾지못함")
	void processPayment_Failure_ReservationNotFound() throws CustomException {
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class,
			() -> paymentApplicationService.processPayment(paymentCommand, queueToken));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESERVATION_NOT_FOUND);
		verify(reservationRepository, times(1)).findById(reservationId);
		verify(seatRepository, never()).findById(any());
		verify(userRepository, never()).findById(any());
		verify(paymentRepository, never()).findByReservationId(any());
		verify(paymentDomainService, never()).processPayment(any(), any(), any(), any());
	}

	@Test
	@DisplayName("결제트랜잭션_실패_좌석정보찾지못함")
	void processPayment_Failure_SeatNotFound() throws CustomException {
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
		when(seatRepository.findById(seatId)).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class,
			() -> paymentApplicationService.processPayment(paymentCommand, queueToken));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SEAT_NOT_FOUND);
		verify(reservationRepository, times(1)).findById(reservationId);
		verify(seatRepository, times(1)).findById(seatId);
		verify(userRepository, never()).findById(any());
		verify(paymentRepository, never()).findByReservationId(any());
		verify(paymentDomainService, never()).processPayment(any(), any(), any(), any());
	}

	@Test
	@DisplayName("결제트랜잭션_실패_유저정보찾지못함")
	void processPayment_Failure_UserNotFound() throws CustomException {
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
		when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class,
			() -> paymentApplicationService.processPayment(paymentCommand, queueToken));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
		verify(reservationRepository, times(1)).findById(reservationId);
		verify(seatRepository, times(1)).findById(seatId);
		verify(userRepository, times(1)).findById(userId);
		verify(seatHoldRepository, never()).isHoldSeat(any(), any());
		verify(paymentRepository, never()).findByReservationId(any());
		verify(paymentDomainService, never()).processPayment(any(), any(), any(), any());
	}

	@Test
	@DisplayName("결제트랜잭션_실패_좌석임시배정끝남")
	void processPayment_Failure_SeatNotHold() throws CustomException {
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
		when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(seatHoldRepository.isHoldSeat(seatId, userId)).thenReturn(false);

		CustomException exception = assertThrows(CustomException.class,
			() -> paymentApplicationService.processPayment(paymentCommand, queueToken));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SEAT_NOT_HOLD);
		verify(reservationRepository, times(1)).findById(reservationId);
		verify(seatRepository, times(1)).findById(seatId);
		verify(userRepository, times(1)).findById(userId);
		verify(seatHoldRepository, times(1)).isHoldSeat(seatId, userId);
		verify(paymentRepository, never()).findByReservationId(any());
		verify(paymentDomainService, never()).processPayment(any(), any(), any(), any());
	}

	@Test
	@DisplayName("결제트랜잭션_실패_결제정보찾지못함")
	void processPaymentTransaction_Failure_PaymentNotFound() throws CustomException {
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
		when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(seatHoldRepository.isHoldSeat(seatId, userId)).thenReturn(true);
		when(paymentRepository.findByReservationId(reservationId)).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class,
			() -> paymentApplicationService.processPayment(paymentCommand, queueToken));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_NOT_FOUND);
		verify(reservationRepository, times(1)).findById(reservationId);
		verify(seatRepository, times(1)).findById(seatId);
		verify(userRepository, times(1)).findById(userId);
		verify(seatHoldRepository, times(1)).isHoldSeat(seatId, userId);
		verify(paymentRepository, times(1)).findByReservationId(reservationId);
		verify(paymentDomainService, never()).processPayment(any(), any(), any(), any());
	}

	@Test
	@DisplayName("결제트랜잭션_실패_도메인서비스예외")
	void processPayment_Failure_DomainServiceException() throws CustomException {
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
		when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(seatHoldRepository.isHoldSeat(seatId, userId)).thenReturn(true);
		when(paymentRepository.findByReservationId(reservationId)).thenReturn(Optional.of(payment));
		when(paymentDomainService.processPayment(reservation, payment, seat, user))
			.thenThrow(new CustomException(ErrorCode.INSUFFICIENT_BALANCE));

		CustomException exception = assertThrows(CustomException.class,
			() -> paymentApplicationService.processPayment(paymentCommand, queueToken));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_BALANCE);
		verify(reservationRepository, times(1)).findById(reservationId);
		verify(seatRepository, times(1)).findById(seatId);
		verify(userRepository, times(1)).findById(userId);
		verify(seatHoldRepository, times(1)).isHoldSeat(seatId, userId);
		verify(paymentRepository, times(1)).findByReservationId(reservationId);
		verify(paymentDomainService, times(1)).processPayment(reservation, payment, seat, user);

		verify(paymentRepository, never()).save(any());
		verify(userRepository, never()).save(any());
		verify(reservationRepository, never()).save(any());
		verify(seatRepository, never()).save(any());
		verify(seatHoldRepository, never()).deleteHold(any(), any());
		verify(queueTokenRepository, never()).expiresQueueToken(any());
	}
}