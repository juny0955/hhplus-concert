package kr.hhplus.be.server.domain.payment.usecase;

import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.payment.port.in.CreatePaymentUseCase;
import kr.hhplus.be.server.domain.payment.port.out.SavePaymentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreatePaymentService implements CreatePaymentUseCase {

	private final SavePaymentPort savePaymentPort;

	@Override
	@Transactional
	public Payment createPayment(UUID userId, UUID reservationId, BigDecimal price) {
		return savePaymentPort.save(Payment.of(userId, reservationId, price));
	}
}
