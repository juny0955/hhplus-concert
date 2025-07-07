package kr.hhplus.be.server.payment.application.interactor;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.payment.application.service.PaymentApplicationService;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.ports.in.CreatePaymentInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreatePaymentInteractor implements CreatePaymentInput {

	private final PaymentApplicationService paymentApplicationService;

	@Override
	public Payment createPayment(UUID userId, UUID reservationId, BigDecimal price) {
		return paymentApplicationService.createPayment(userId, reservationId, price);
	}
}
