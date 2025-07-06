package kr.hhplus.be.server.reservation.domain;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.payment.domain.Payment;

public record ReservationDomainResult(
	Seat seat,
	Reservation reservation,
	Payment payment
) {
}
