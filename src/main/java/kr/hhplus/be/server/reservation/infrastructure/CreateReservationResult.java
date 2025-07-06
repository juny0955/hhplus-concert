package kr.hhplus.be.server.reservation.infrastructure;

import java.util.UUID;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.concert.domain.seat.Seat;

public record CreateReservationResult(
	Reservation reservation,
	Payment payment,
	Seat seat,
	UUID userId
) {
}
