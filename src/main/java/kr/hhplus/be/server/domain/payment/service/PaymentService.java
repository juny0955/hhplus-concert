package kr.hhplus.be.server.domain.payment.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.payment.port.out.PaymentRepository;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;

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
