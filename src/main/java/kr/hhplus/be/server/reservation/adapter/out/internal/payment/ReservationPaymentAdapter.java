package kr.hhplus.be.server.reservation.adapter.out.internal.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.port.in.CancelPaymentUseCase;
import kr.hhplus.be.server.payment.port.in.CreatePaymentUseCase;
import kr.hhplus.be.server.reservation.port.out.PaymentQueryPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationPaymentAdapter implements PaymentQueryPort {

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
