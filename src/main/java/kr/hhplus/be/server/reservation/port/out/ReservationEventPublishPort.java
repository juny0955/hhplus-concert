package kr.hhplus.be.server.reservation.port.out;

import kr.hhplus.be.server.reservation.domain.PaidReservationEvent;

public interface ReservationEventPublishPort {
	void publishPaidReservationEvent(PaidReservationEvent event);
}
