package kr.hhplus.be.server.payment.application.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.in.seat.PaidSeatInput;
import kr.hhplus.be.server.concert.ports.out.SeatHoldRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentTransactionResult;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainResult;
import kr.hhplus.be.server.payment.ports.in.PaymentCommand;
import kr.hhplus.be.server.payment.ports.out.PaymentRepository;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.out.QueueTokenRepository;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.ports.out.ReservationRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.ports.in.UsePointInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentApplicationService {

	private final QueueTokenRepository queueTokenRepository;
	private final ReservationRepository reservationRepository;
	private final UsePointInput usePointInput;
	private final PaidSeatInput paidSeatInput;
	private final PaymentRepository paymentRepository;
	private final SeatHoldRepository seatHoldRepository;

	@Transactional
	public PaymentTransactionResult processPayment(PaymentCommand command, QueueToken queueToken) throws Exception {
		Reservation reservation = getReservation(command.reservationId());

		validateSeatHold(reservation.seatId(), queueToken.userId());

		Payment payment = getPayment(reservation.id());

		Payment savedPayment 	 = paymentRepository.save(result.payment());
		User savedUser           = usePointInput.usePoint(userId, result.payment().amount());
		Reservation savedReservation = reservationRepository.save(reservation.paid());
		Seat savedSeat       	 = paidSeatInput.paidSeat(reservation.seatId());

		seatHoldRepository.deleteHold(savedSeat.id(), savedUser.id());
		queueTokenRepository.expiresQueueToken(queueToken.tokenId().toString());

		return new PaymentTransactionResult(savedPayment, savedReservation, savedSeat, savedUser);
	}

	public Payment getPayment(UUID reservationId) throws CustomException {
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

	@Transactional
	public Payment createPayment(UUID userId, UUID reservationId, BigDecimal price) {
		return paymentRepository.save(Payment.of(userId, reservationId, price));
	}

	@Transactional
	public Payment expirePayment(UUID reservationId) throws CustomException {
		Payment payment = getPayment(reservationId);
		return paymentRepository.save(payment.expired());
	}
}
