package kr.hhplus.be.server.domain.reservation.domain;

import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReservationCreatedEvent(
	UUID reservationId,
	UUID userId,
	UUID paymentId,
	UUID seatId,
	BigDecimal amount,
	LocalDateTime occurredAt
) {

	public static ReservationCreatedEvent from(Reservation reservation, Payment payment, Seat seat, UUID userId) {
		return ReservationCreatedEvent.builder()
			.reservationId(reservation.id())
			.userId(userId)
			.paymentId(payment.id())
			.seatId(seat.id())
			.amount(seat.price())
			.occurredAt(reservation.createdAt())
			.build();
	}
}
