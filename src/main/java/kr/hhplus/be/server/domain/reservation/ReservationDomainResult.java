package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.payment.Payment;

public record ReservationDomainResult(
	Seat seat,
	Payment payment,
	Reservation reservation
) {
}
