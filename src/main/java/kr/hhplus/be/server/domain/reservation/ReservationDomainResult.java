package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.payment.Payment;

public record ReservationDomainResult(
	Seat seat,
	Reservation reservation,
	Payment payment
) {
}
