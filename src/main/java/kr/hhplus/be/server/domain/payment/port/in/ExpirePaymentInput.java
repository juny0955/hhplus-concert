package kr.hhplus.be.server.domain.payment.port.in;

import java.util.UUID;

import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.common.exception.CustomException;

public interface ExpirePaymentInput {
	Payment expirePayment(UUID reservationId) throws CustomException;
}
