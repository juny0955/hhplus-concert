package kr.hhplus.be.server.domain.payment.port.in;

import kr.hhplus.be.server.domain.payment.domain.Payment;

public interface PaymentUseCase {
	Payment pay(PaymentCommand commend) throws Exception;
}
