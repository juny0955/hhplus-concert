package kr.hhplus.be.server.domain.event.reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.domain.event.Event;

public record ReservationCreatedEvent(
	UUID reservationId,
	UUID userId,
	UUID concertId,
	UUID seatId,
	BigDecimal amount,
	LocalDateTime expiresAt,
	LocalDateTime occurredAt
) implements Event {

	@Override
	public String getKey() {
		return reservationId.toString();
	}
}
