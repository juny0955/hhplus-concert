package kr.hhplus.be.server.dataplatform.port.in;

import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;

public interface PaymentSuccessUseCase {
	void sendDataPlatform(PaymentSuccessEvent event);
}
