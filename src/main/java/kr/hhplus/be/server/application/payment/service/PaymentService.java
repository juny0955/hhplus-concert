package kr.hhplus.be.server.application.payment.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.application.payment.port.in.PaymentCommand;
import kr.hhplus.be.server.application.payment.port.out.PaymentRepository;
import kr.hhplus.be.server.application.queue.port.in.ExpireQueueTokenInput;
import kr.hhplus.be.server.application.reservation.port.in.PaidReservationInput;
import kr.hhplus.be.server.application.seat.port.in.PaidSeatInput;
import kr.hhplus.be.server.application.seatHold.port.in.ReleaseSeatHoldInput;
import kr.hhplus.be.server.application.user.port.in.UsePointUseCase;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentService {

	private final ExpireQueueTokenInput expireQueueTokenInput;
	private final PaidReservationInput paidReservationInput;
	private final UsePointUseCase usePointUseCase;
	private final PaidSeatInput paidSeatInput;
	private final PaymentRepository paymentRepository;
	private final ReleaseSeatHoldInput releaseSeatHoldInput;

	@Transactional
	public PaymentResult processPayment(PaymentCommand command, QueueToken queueToken) throws Exception {
		Payment payment = getPayment(command.reservationId());

		Payment savedPayment		 = paymentRepository.save(payment.success());
		User savedUser          	 = usePointUseCase.usePoint(queueToken.userId(), payment.amount());
		Reservation savedReservation = paidReservationInput.paidReservation(command.reservationId());
		Seat savedSeat       	 	 = paidSeatInput.paidSeat(savedReservation.seatId());

		releaseSeatHoldInput.releaseSeatHold(savedSeat.id(), savedUser.id());
		expireQueueTokenInput.expireQueueToken(queueToken.tokenId().toString());

		return new PaymentResult(savedPayment, savedSeat, savedReservation, savedUser);
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
