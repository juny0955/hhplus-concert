package kr.hhplus.be.server.domain.payment.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.port.out.GetPaymentPort;
import kr.hhplus.be.server.domain.payment.port.out.PaymentRepository;
import kr.hhplus.be.server.domain.payment.port.out.SavePaymentPort;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentJpaGateway implements PaymentRepository, GetPaymentPort, SavePaymentPort {

	private final JpaPaymentRepository jpaPaymentRepository;

	@Override
	public Payment getPaymentByReservationId(UUID reservationId) throws CustomException {
		return jpaPaymentRepository.findByReservationId(reservationId.toString())
			.map(PaymentEntity::toDomain)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
	}

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
