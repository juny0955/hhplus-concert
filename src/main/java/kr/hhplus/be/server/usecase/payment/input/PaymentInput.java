package kr.hhplus.be.server.usecase.payment.input;

import kr.hhplus.be.server.framework.exception.CustomException;

public interface PaymentInput {
	void payment(PaymentCommand commend) throws CustomException;
}
