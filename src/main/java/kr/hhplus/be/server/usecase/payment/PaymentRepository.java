package kr.hhplus.be.server.usecase.payment;

import kr.hhplus.be.server.domain.payment.Payment;

public interface PaymentRepository {
	void save(Payment payment);
}
