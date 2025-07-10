package kr.hhplus.be.server.dataplatform.port.out;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;

public interface SendReservationDataPort {
	void send(PaymentSuccessEvent event, Concert concert);
}
