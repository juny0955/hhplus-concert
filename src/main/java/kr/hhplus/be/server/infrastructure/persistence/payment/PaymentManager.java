package kr.hhplus.be.server.infrastructure.persistence.payment;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentDomainResult;
import kr.hhplus.be.server.domain.payment.PaymentDomainService;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.queue.QueueTokenRepository;
import kr.hhplus.be.server.domain.queue.QueueTokenUtil;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatHoldRepository;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.usecase.payment.input.PaymentCommand;
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

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public PaymentTransactionResult processPayment(PaymentCommand command) throws CustomException {
		QueueToken queueToken = getQueueTokenAndValid(command.queueTokenId());

		Reservation reservation = getReservation(command.reservationId());
		Seat seat = getSeat(reservation.seatId());
		User user = getUser(queueToken.userId());

		validateSeatHold(seat.id(), user.id());

		Payment payment = getPaymentWithLock(reservation.id());
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

	private Payment getPaymentWithLock(UUID reservationId) throws CustomException {
		return paymentRepository.findByReservationIdForUpdate(reservationId)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
	}

	private Reservation getReservation(UUID reservationId) throws CustomException {
		return reservationRepository.findById(reservationId)
			.orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
	}

	private QueueToken getQueueTokenAndValid(String tokenId) throws CustomException {
		QueueToken queueToken = queueTokenRepository.findQueueTokenByTokenId(tokenId);
		QueueTokenUtil.validateActiveQueueToken(queueToken);
		return queueToken;
	}

	private void validateSeatHold(UUID seatId, UUID userId) throws CustomException {
		if (!seatHoldRepository.isHoldSeat(seatId, userId))
			throw new CustomException(ErrorCode.SEAT_NOT_HOLD);
	}
}
