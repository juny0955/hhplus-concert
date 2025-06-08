package kr.hhplus.be.server.usecase.reservation.input;

import kr.hhplus.be.server.framework.exception.CustomException;

public interface ReservationInput {
	void reserveSeat(ReserveSeatCommand command) throws CustomException;
}
