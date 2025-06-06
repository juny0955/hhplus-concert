package kr.hhplus.be.server.usecase.reservation.input;

import java.util.UUID;

import kr.hhplus.be.server.interfaces.api.concert.dto.request.ReservationRequest;

public record ReserveSeatCommand(
	UUID concertId,
	UUID concertDateId,
	UUID seatId,
	String queueTokenId
){
	public static ReserveSeatCommand of(UUID concertId, ReservationRequest request, String queueToken) {
		return new ReserveSeatCommand(concertId, request.seatId(), request.concertDateId(), queueToken);
	}
}
