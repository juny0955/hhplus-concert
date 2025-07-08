package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.ports.in.seat.PaidSeatInput;
import kr.hhplus.be.server.concert.ports.in.seatHold.ReleaseSeatHoldInput;
import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.common.framework.exception.ErrorCode;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentTransactionResult;
import kr.hhplus.be.server.payment.ports.in.PaymentCommand;
import kr.hhplus.be.server.payment.ports.out.PaymentRepository;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.in.ExpireQueueTokenInput;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.ports.in.PaidReservationInput;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.ports.in.UsePointInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentApplicationService {

	private final ExpireQueueTokenInput expireQueueTokenInput;
	private final PaidReservationInput paidReservationInput;
	private final UsePointInput usePointInput;
	private final PaidSeatInput paidSeatInput;
	private final PaymentRepository paymentRepository;
	private final ReleaseSeatHoldInput releaseSeatHoldInput;

	@Transactional
	public PaymentTransactionResult processPayment(PaymentCommand command, QueueToken queueToken) throws Exception {
		Payment payment = getPayment(command.reservationId());

		Payment savedPayment		 = paymentRepository.save(payment.success());
		User savedUser          	 = usePointInput.usePoint(queueToken.userId(), payment.amount());
		Reservation savedReservation = paidReservationInput.paidReservation(command.reservationId());
		Seat savedSeat       	 	 = paidSeatInput.paidSeat(savedReservation.seatId());

		releaseSeatHoldInput.releaseSeatHold(savedSeat.id(), savedUser.id());
		expireQueueTokenInput.expireQueueToken(queueToken.tokenId().toString());

		return new PaymentTransactionResult(savedPayment, savedReservation, savedSeat, savedUser);
	}

	public Payment getPayment(UUID reservationId) throws CustomException {
		return paymentRepository.findByReservationId(reservationId)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
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
