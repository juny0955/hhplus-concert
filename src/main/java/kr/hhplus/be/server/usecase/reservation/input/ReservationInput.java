package kr.hhplus.be.server.usecase.reservation.input;

import kr.hhplus.be.server.usecase.exception.CustomException;

public interface ReservationInput {
	void reserveSeat(ReserveSeatCommand command) throws CustomException;
}
