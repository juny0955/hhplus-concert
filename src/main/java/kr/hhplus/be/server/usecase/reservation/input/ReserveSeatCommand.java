package kr.hhplus.be.server.usecase.reservation.input;

import java.util.UUID;

import kr.hhplus.be.server.api.reservation.dto.request.ReservationRequest;

public record ReserveSeatCommand(
	UUID concertId,
	UUID concertDateId,
	UUID seatId,
	String queueTokenId
){
	public static ReserveSeatCommand of(ReservationRequest request, UUID seatId, String queueToken) {
		return new ReserveSeatCommand(request.concertId(), request.concertDateId(), seatId, queueToken);
	}
}
