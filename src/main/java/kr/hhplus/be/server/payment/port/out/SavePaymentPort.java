package kr.hhplus.be.server.payment.port.out;

import kr.hhplus.be.server.payment.domain.Payment;

public interface SavePaymentPort {
	Payment save(Payment payment);
}
