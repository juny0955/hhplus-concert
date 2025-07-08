package kr.hhplus.be.server.application.payment.port.in;

import kr.hhplus.be.server.application.payment.dto.PaymentResult;

public interface PaymentUseCase {
	PaymentResult payment(PaymentCommand commend) throws Exception;
}
