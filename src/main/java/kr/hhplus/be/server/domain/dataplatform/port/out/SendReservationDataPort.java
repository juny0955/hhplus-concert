package kr.hhplus.be.server.domain.dataplatform.port.out;

import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.payment.domain.PaymentSuccessEvent;

public interface SendReservationDataPort {
	void send(PaymentSuccessEvent event, Concert concert);
}
