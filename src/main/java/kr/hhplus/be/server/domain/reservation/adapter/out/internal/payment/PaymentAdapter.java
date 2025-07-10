package kr.hhplus.be.server.domain.reservation.adapter.out.internal.payment;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.payment.port.in.CancelPaymentUseCase;
import kr.hhplus.be.server.domain.payment.port.in.CreatePaymentUseCase;
import kr.hhplus.be.server.domain.reservation.port.out.PaymentQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentAdapter implements PaymentQueryPort {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final CancelPaymentUseCase cancelPaymentUseCase;

    @Override
    public Payment createPayment(UUID userId, UUID reservationId, BigDecimal price) {
        return createPaymentUseCase.createPayment(userId, reservationId, price);
    }

    @Override
    public Payment cancelPayment(UUID reservationId) throws CustomException {
        return cancelPaymentUseCase.cancelPayment(reservationId);
    }
}
