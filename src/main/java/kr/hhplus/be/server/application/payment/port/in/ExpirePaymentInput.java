package kr.hhplus.be.server.application.payment.port.in;

import java.util.UUID;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.exception.CustomException;

public interface ExpirePaymentInput {
	Payment expirePayment(UUID reservationId) throws CustomException;
}
