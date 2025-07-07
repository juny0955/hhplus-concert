package kr.hhplus.be.server.reservation.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record ReservationDataRequest(
	UUID reservationId,
	UUID seatId,
	BigDecimal amount,
	LocalDateTime reservedAt
) {
	public static ReservationDataRequest from(ReservationCreatedEvent event) {
		return ReservationDataRequest.builder()
			.reservationId(event.reservationId())
			.seatId(event.seatId())
			.amount(event.amount())
			.reservedAt(event.occurredAt())
			.build();
	}
}
