package kr.hhplus.be.server.payment.port.in;

import kr.hhplus.be.server.payment.domain.Payment;

public interface PaymentUseCase {
	Payment pay(PaymentCommand commend) throws Exception;
}
