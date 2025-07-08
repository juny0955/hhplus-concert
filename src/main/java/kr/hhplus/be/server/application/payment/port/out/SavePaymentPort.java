package kr.hhplus.be.server.application.payment.port.out;

import kr.hhplus.be.server.domain.payment.Payment;

public interface SavePaymentPort {
	Payment save(Payment payment);
}
