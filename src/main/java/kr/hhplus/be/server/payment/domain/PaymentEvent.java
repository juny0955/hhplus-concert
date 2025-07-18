package kr.hhplus.be.server.payment.domain;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.payment.port.in.PaymentCommand;
import lombok.Builder;

@Builder
public record PaymentEvent(
	UUID paymentId,
	UUID reservationId,
	UUID seatId,
	UUID userId,
	UUID tokenId,
	BigDecimal amount
) {
	public static PaymentEvent of(PaymentCommand command, Payment payment, UUID userId, UUID tokenId) {
		return PaymentEvent.builder()
			.paymentId(payment.id())
			.reservationId(command.reservationId())
			.seatId(command.seatId())
			.userId(userId)
			.tokenId(tokenId)
			.amount(payment.amount())
			.build();
	}
}
