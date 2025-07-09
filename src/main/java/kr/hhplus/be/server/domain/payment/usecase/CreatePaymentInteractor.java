package kr.hhplus.be.server.domain.payment.usecase;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.port.in.CreatePaymentInput;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreatePaymentInteractor implements CreatePaymentInput {

	private final PaymentService paymentService;

	@Override
	public Payment createPayment(UUID userId, UUID reservationId, BigDecimal price) {
		return paymentService.createPayment(userId, reservationId, price);
	}
}
