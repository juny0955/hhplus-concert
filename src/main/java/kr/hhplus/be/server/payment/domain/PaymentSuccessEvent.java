package kr.hhplus.be.server.payment.domain;

import java.util.UUID;

import kr.hhplus.be.server.concert.domain.seat.CompletePaymentEvent;
import lombok.Builder;

@Builder
public record PaymentSuccessEvent(
	UUID paymentId,
	UUID reservationId,
	UUID seatId,
	UUID userId,
	UUID tokenId
) {
	public static PaymentSuccessEvent from(CompletePaymentEvent event) {
		return PaymentSuccessEvent.builder()
			.paymentId(event.paymentId())
			.reservationId(event.reservationId())
			.seatId(event.seatId())
			.userId(event.userId())
			.tokenId(event.tokenId())
			.build();
	}
}
