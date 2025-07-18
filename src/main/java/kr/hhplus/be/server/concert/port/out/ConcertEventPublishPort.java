package kr.hhplus.be.server.concert.port.out;

import kr.hhplus.be.server.concert.domain.seat.CompletePaymentEvent;

public interface ConcertEventPublishPort {
	void publishCompletePaymentEvent(CompletePaymentEvent event);
}
