package kr.hhplus.be.server.reservation.ports.in;

import kr.hhplus.be.server.reservation.application.dto.ReserveSeatResult;

public interface ReservationCreateInput {
	ReserveSeatResult reserveSeat(ReserveSeatCommand command) throws Exception;
}
