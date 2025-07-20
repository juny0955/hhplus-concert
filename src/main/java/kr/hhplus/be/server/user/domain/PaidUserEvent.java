package kr.hhplus.be.server.user.domain;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.payment.domain.PaymentEvent;
import lombok.Builder;

@Builder
public record PaidUserEvent(
	UUID paymentId,
	UUID reservationId,
	UUID seatId,
	UUID userId,
	UUID tokenId,
	BigDecimal amount
) {
	public static PaidUserEvent of(PaymentEvent event) {
		return PaidUserEvent.builder()
			.paymentId(event.paymentId())
			.reservationId(event.reservationId())
			.seatId(event.seatId())
			.userId(event.userId())
			.tokenId(event.tokenId())
			.amount(event.amount())
			.build();
	}
}
