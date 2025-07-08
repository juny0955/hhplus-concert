package kr.hhplus.be.server.application.reservation.port.in;

import java.util.UUID;

import kr.hhplus.be.server.adapters.in.web.reservation.request.ReservationRequest;

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
