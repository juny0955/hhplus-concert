package kr.hhplus.be.server.domain.payment.port.in;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.domain.payment.domain.Payment;

public interface CreatePaymentUseCase {
	Payment createPayment(UUID userId, UUID reservationId, BigDecimal price);
}
