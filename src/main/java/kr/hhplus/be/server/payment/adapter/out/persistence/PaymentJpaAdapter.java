package kr.hhplus.be.server.payment.adapter.out.persistence;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.port.out.GetPaymentPort;
import kr.hhplus.be.server.payment.port.out.SavePaymentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentJpaAdapter implements GetPaymentPort, SavePaymentPort {

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
}
