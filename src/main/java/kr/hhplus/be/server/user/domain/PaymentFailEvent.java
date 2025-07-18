package kr.hhplus.be.server.user.domain;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.payment.domain.PaymentEvent;
import kr.hhplus.be.server.reservation.domain.PaidUserFailEvent;
import lombok.Builder;

@Builder
public record PaymentFailEvent (
	UUID paymentId,
	UUID reservationId,
	UUID seatId,
	UUID userId,
	UUID tokenId,
	BigDecimal amount,
	String failureReason
) {
	public static PaymentFailEvent of(PaymentEvent event, String reason) {
		return PaymentFailEvent.builder()
			.paymentId(event.paymentId())
			.reservationId(event.reservationId())
			.seatId(event.seatId())
			.userId(event.userId())
			.tokenId(event.tokenId())
			.amount(event.amount())
			.failureReason(reason)
			.build();
	}

	public static PaymentFailEvent of(PaidUserFailEvent event) {
		return PaymentFailEvent.builder()
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
