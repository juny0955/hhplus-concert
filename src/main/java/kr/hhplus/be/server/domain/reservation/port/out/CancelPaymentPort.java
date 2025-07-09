package kr.hhplus.be.server.domain.reservation.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.payment.domain.Payment;

import java.util.UUID;

public interface CancelPaymentPort {
    Payment cancelPayment(UUID reservationId) throws CustomException;
}
