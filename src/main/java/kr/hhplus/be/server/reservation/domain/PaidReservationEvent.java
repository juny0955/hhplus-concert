package kr.hhplus.be.server.reservation.domain;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.user.domain.PaidUserEvent;
import lombok.Builder;

@Builder
public record PaidReservationEvent(
	UUID paymentId,
	UUID reservationId,
	UUID seatId,
	UUID userId,
	UUID tokenId,
	BigDecimal amount
) {
	public static PaidReservationEvent from(PaidUserEvent event) {
		return PaidReservationEvent.builder()
			.paymentId(event.paymentId())
			.reservationId(event.reservationId())
			.seatId(event.seatId())
			.userId(event.userId())
			.tokenId(event.tokenId())
			.amount(event.amount())
			.build();
	}
}
