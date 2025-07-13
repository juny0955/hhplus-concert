package kr.hhplus.be.server.payment.port.out;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;

public interface DataPlatformOutPort {
	void send(PaymentSuccessEvent event, Concert concert);
}
