package kr.hhplus.be.server.infrastructure.persistence.payment;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentDomainResult;
import kr.hhplus.be.server.payment.domain.PaymentDomainService;
import kr.hhplus.be.server.payment.domain.PaymentRepository;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.domain.QueueTokenRepository;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationRepository;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.SeatHoldRepository;
import kr.hhplus.be.server.concert.domain.seat.SeatRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.domain.UserRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.payment.usecase.input.PaymentCommand;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentManager {

	private final QueueTokenRepository queueTokenRepository;
	private final ReservationRepository reservationRepository;
	private final UserRepository userRepository;
	private final SeatRepository seatRepository;
	private final PaymentRepository paymentRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final PaymentDomainService paymentDomainService;

	@Transactional
	public PaymentTransactionResult processPayment(PaymentCommand command, QueueToken queueToken) throws CustomException {
		Reservation reservation = getReservation(command.reservationId());
		Seat seat = getSeat(reservation.seatId());
		User user = getUser(queueToken.userId());

		validateSeatHold(seat.id(), user.id());

		Payment payment = getPayment(reservation.id());
		PaymentDomainResult result = paymentDomainService.processPayment(reservation, payment, seat, user);

		PaymentTransactionResult paymentTransactionResult = processPayment(result);

		seatHoldRepository.deleteHold(paymentTransactionResult.seat().id(), paymentTransactionResult.user().id());
		queueTokenRepository.expiresQueueToken(queueToken.tokenId().toString());

		return paymentTransactionResult;
	}

	private PaymentTransactionResult processPayment(PaymentDomainResult result) {
		Payment savedPayment	 = paymentRepository.save(result.payment());
		User savedUser           = userRepository.save(result.user());
		Reservation savedReservation = reservationRepository.save(result.reservation());
		Seat savedSeat        = seatRepository.save(result.seat());

		return new PaymentTransactionResult(savedPayment, savedReservation, savedSeat, savedUser);
	}

	private User getUser(UUID userId) throws CustomException {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	private Seat getSeat(UUID seatId) throws CustomException {
		return seatRepository.findById(seatId)
			.orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
	}

	private Payment getPayment(UUID reservationId) throws CustomException {
		return paymentRepository.findByReservationId(reservationId)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
	}

	private Reservation getReservation(UUID reservationId) throws CustomException {
		return reservationRepository.findById(reservationId)
			.orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
	}

	private void validateSeatHold(UUID seatId, UUID userId) throws CustomException {
		if (!seatHoldRepository.isHoldSeat(seatId, userId))
			throw new CustomException(ErrorCode.SEAT_NOT_HOLD);
	}
}
