package kr.hhplus.be.server.payment.usecase.input;

public interface PaymentInput {
	void payment(PaymentCommand commend) throws Exception;
}
