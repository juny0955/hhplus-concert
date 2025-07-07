package kr.hhplus.be.server.payment.ports.in;

import java.util.UUID;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.payment.domain.Payment;

public interface ExpirePaymentInput {
	Payment expirePayment(UUID reservationId) throws CustomException;
}
