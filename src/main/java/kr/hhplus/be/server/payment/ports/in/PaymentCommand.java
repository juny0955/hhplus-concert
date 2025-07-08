package kr.hhplus.be.server.payment.ports.in;

import java.util.UUID;

public record PaymentCommand(
	UUID reservationId,
	UUID seatId,
	String queueTokenId
) {
	public static PaymentCommand of(UUID reservationId, UUID seatId, String queueToken) {
		return new PaymentCommand(reservationId, seatId, queueToken);
	}
}
