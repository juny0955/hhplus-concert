package kr.hhplus.be.server.payment.port.out;

import kr.hhplus.be.server.payment.domain.PaymentEvent;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;

public interface EventPublishPort {
	void publishPaymentEvent(PaymentEvent event);
	void publishPaymentSuccessEvent(PaymentSuccessEvent event);
}
