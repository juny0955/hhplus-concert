package kr.hhplus.be.server.domain.payment.port.in;

import kr.hhplus.be.server.domain.payment.dto.PaymentResult;

public interface PaymentUseCase {
	PaymentResult payment(PaymentCommand commend) throws Exception;
}
