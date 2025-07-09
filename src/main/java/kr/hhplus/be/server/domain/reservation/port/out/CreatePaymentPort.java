package kr.hhplus.be.server.domain.reservation.port.out;

import kr.hhplus.be.server.domain.payment.domain.Payment;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreatePaymentPort {
    Payment createPayment(UUID userId, UUID reservationId, BigDecimal price);
}
