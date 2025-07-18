package kr.hhplus.be.server.reservation.domain;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.concert.domain.seat.PaidReservationFailEvent;
import kr.hhplus.be.server.user.domain.PaidUserEvent;
import lombok.Builder;

@Builder
public record PaidUserFailEvent(
	UUID paymentId,
	UUID reservationId,
	UUID seatId,
	UUID userId,
	UUID tokenId,
	BigDecimal amount,
	String failureReason
) {
	public static PaidUserFailEvent of(PaidUserEvent event, String reason) {
		return PaidUserFailEvent.builder()
			.paymentId(event.paymentId())
			.reservationId(event.reservationId())
			.seatId(event.seatId())
			.userId(event.userId())
			.tokenId(event.tokenId())
			.amount(event.amount())
			.failureReason(reason)
			.build();
	}

	public static PaidUserFailEvent from(PaidReservationFailEvent event) {
		return PaidUserFailEvent.builder()
			.paymentId(event.paymentId())
			.reservationId(event.reservationId())
			.seatId(event.seatId())
			.userId(event.userId())
			.tokenId(event.tokenId())
			.amount(event.amount())
			.failureReason(event.failureReason())
			.build();
	}
}
