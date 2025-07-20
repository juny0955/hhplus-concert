package kr.hhplus.be.server.concert.domain.seat;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.reservation.domain.PaidReservationEvent;
import lombok.Builder;

@Builder
public record CompletePaymentEvent(
	UUID paymentId,
	UUID reservationId,
	UUID seatId,
	UUID userId,
	UUID tokenId,
	BigDecimal amount
) {
	public static CompletePaymentEvent from(PaidReservationEvent event) {
		return CompletePaymentEvent.builder()
			.paymentId(event.paymentId())
			.reservationId(event.reservationId())
			.seatId(event.seatId())
			.userId(event.userId())
			.tokenId(event.tokenId())
			.amount(event.amount())
			.build();
	}
}
