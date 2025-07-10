package kr.hhplus.be.server.reservation.domain;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReservationCreatedEvent(
	Reservation reservation,
	Payment payment,
	Seat seat,
	UUID userId,
	UUID concertId,
	LocalDateTime occurredAt
) {

	public static ReservationCreatedEvent from(Reservation reservation, Payment payment, Seat seat, UUID userId, UUID concertId) {
		return ReservationCreatedEvent.builder()
			.reservation(reservation)
			.payment(payment)
			.seat(seat)
			.userId(userId)
			.concertId(concertId)
			.occurredAt(reservation.createdAt())
			.build();
	}
}
