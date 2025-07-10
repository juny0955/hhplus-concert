package kr.hhplus.be.server.reservation.port.in;

import java.util.UUID;

import kr.hhplus.be.server.reservation.adapter.in.web.reservation.request.ReservationRequest;

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
