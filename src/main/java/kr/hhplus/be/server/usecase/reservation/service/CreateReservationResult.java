package kr.hhplus.be.server.usecase.reservation.service;

import java.util.UUID;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.seat.Seat;

public record CreateReservationResult(
	Reservation reservation,
	Payment payment,
	Seat seat,
	UUID userId
) {
}
