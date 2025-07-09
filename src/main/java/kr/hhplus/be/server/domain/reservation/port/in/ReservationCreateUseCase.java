package kr.hhplus.be.server.domain.reservation.port.in;

import kr.hhplus.be.server.domain.reservation.dto.ReserveSeatResult;

public interface ReservationCreateUseCase {
	ReserveSeatResult reserveSeat(ReserveSeatCommand command) throws Exception;
}
