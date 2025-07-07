package kr.hhplus.be.server.payment.ports.in;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.payment.domain.Payment;

public interface CreatePaymentInput {
	Payment createPayment(UUID userId, UUID reservationId, BigDecimal price);
}
