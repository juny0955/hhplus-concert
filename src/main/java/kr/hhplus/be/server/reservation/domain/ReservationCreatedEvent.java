package kr.hhplus.be.server.reservation.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.reservation.application.dto.CreateReservationResult;
import lombok.Builder;

@Builder
public record ReservationCreatedEvent(
	UUID reservationId,
	UUID userId,
	UUID paymentId,
	UUID seatId,
	BigDecimal amount,
	LocalDateTime occurredAt
) {

	public static ReservationCreatedEvent from(CreateReservationResult result) {
		LocalDateTime now = LocalDateTime.now();

		return ReservationCreatedEvent.builder()
			.reservationId(result.reservation().id())
			.userId(result.userId())
			.paymentId(result.payment().id())
			.seatId(result.payment().id())
			.amount(result.payment().amount())
			.occurredAt(now)
			.build();
	}
}
