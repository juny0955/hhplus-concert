package kr.hhplus.be.server.domain.reservation.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.payment.domain.Payment;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentQueryPort {
    Payment createPayment(UUID userId, UUID reservationId, BigDecimal price);
    Payment cancelPayment(UUID reservationId) throws CustomException;
}
