package kr.hhplus.be.server.concert.port.out;

import kr.hhplus.be.server.concert.domain.seat.CompletePaymentEvent;
import kr.hhplus.be.server.concert.domain.seat.PaidReservationFailEvent;

public interface ConcertEventPublishPort {
	void publishCompletePaymentEvent(CompletePaymentEvent event);
	void publishPaidReservationFailEvent(PaidReservationFailEvent event);
}
