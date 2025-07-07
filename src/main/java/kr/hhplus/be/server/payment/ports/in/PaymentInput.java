package kr.hhplus.be.server.payment.ports.in;

public interface PaymentInput {
	void payment(PaymentCommand commend) throws Exception;
}
