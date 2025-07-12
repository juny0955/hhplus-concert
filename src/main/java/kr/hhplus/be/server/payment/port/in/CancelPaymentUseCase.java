package kr.hhplus.be.server.payment.port.in;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.payment.domain.Payment;

import java.util.UUID;

public interface CancelPaymentUseCase {
    Payment cancelPayment(UUID reservationId) throws CustomException;
}
