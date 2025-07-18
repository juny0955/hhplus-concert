package kr.hhplus.be.server.queue.port.in;

import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;

public interface ExpireQueueTokenUseCase {
	void expireQueueToken(PaymentSuccessEvent event);
}
