package kr.hhplus.be.server.payment.ports.in;

import kr.hhplus.be.server.payment.application.dto.PaymentResult;

public interface PaymentInput {
	PaymentResult payment(PaymentCommand commend) throws Exception;
}
