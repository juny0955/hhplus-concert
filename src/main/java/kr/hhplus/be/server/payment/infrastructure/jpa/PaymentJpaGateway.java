package kr.hhplus.be.server.payment.infrastructure.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentJpaGateway implements PaymentRepository {

	private final JpaPaymentRepository jpaPaymentRepository;

	@Override
	public Payment save(Payment payment) {
		PaymentEntity paymentEntity = PaymentEntity.from(payment);
		return jpaPaymentRepository.save(paymentEntity).toDomain();
	}

	@Override
	public Optional<Payment> findByReservationId(UUID reservationId) {
		return jpaPaymentRepository.findByReservationId(reservationId.toString())
			.map(PaymentEntity::toDomain);
	}

	@Override
	public void deleteAll() {
		jpaPaymentRepository.deleteAll();
	}
}
