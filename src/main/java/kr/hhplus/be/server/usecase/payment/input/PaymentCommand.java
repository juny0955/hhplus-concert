package kr.hhplus.be.server.usecase.payment.input;

import java.util.UUID;

public record PaymentCommand(
	UUID reservationId,
	String queueTokenId
) {
	public static PaymentCommand of(UUID reservationId, String queueToken) {
		return new PaymentCommand(reservationId, queueToken);
	}
}
