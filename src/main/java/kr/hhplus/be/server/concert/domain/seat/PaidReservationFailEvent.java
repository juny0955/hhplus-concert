package kr.hhplus.be.server.concert.domain.seat;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.reservation.domain.PaidReservationEvent;
import lombok.Builder;

@Builder
public record PaidReservationFailEvent(
	UUID paymentId,
	UUID reservationId,
	UUID seatId,
	UUID userId,
	UUID tokenId,
	BigDecimal amount,
	String failureReason
) {
	public static PaidReservationFailEvent of(PaidReservationEvent event, String reason) {
		return PaidReservationFailEvent.builder()
			.paymentId(event.paymentId())
			.reservationId(event.reservationId())
			.seatId(event.seatId())
			.userId(event.userId())
			.tokenId(event.tokenId())
			.amount(event.amount())
			.failureReason(reason)
			.build();
	}
}
