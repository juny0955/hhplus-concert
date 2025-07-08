package kr.hhplus.be.server.application.reservation.port.in;

import kr.hhplus.be.server.application.reservation.dto.ReserveSeatResult;

public interface ReservationCreateUseCase {
	ReserveSeatResult reserveSeat(ReserveSeatCommand command) throws Exception;
}
