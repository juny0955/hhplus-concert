package kr.hhplus.be.server.interfaces.gateway.repository.payment;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.usecase.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentJpaGateway implements PaymentRepository {

	private final JpaPaymentRepository jpaPaymentRepository;

	@Override
	public Payment save(Payment payment) {
		PaymentEntity paymentEntity = PaymentEntity.from(payment);
		PaymentEntity save = jpaPaymentRepository.save(paymentEntity);
		return Payment.from(save);
	}
}
