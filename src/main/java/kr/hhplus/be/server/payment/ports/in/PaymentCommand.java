package kr.hhplus.be.server.payment.ports.in;

import java.util.UUID;

public record PaymentCommand(
	UUID reservationId,
	String queueTokenId
) {
	public static PaymentCommand of(UUID reservationId, String queueToken) {
		return new PaymentCommand(reservationId, queueToken);
	}
}
