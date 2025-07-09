package kr.hhplus.be.server.domain.reservation.dto;

import java.util.UUID;

import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.seat.domain.Seat;

public record CreateReservationResult(
	Reservation reservation,
	Payment payment,
	Seat seat,
	UUID userId
) {
}
