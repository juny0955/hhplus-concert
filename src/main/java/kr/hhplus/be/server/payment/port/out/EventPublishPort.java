package kr.hhplus.be.server.payment.port.out;

import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;

public interface EventPublishPort {
	void publishPaymentSuccessEvent(PaymentSuccessEvent event);
}
