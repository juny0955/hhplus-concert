package kr.hhplus.be.server.domain.payment.port.out;

import kr.hhplus.be.server.domain.payment.domain.Payment;

public interface SavePaymentPort {
	Payment save(Payment payment);
}
