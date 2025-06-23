package kr.hhplus.be.server.usecase.reservation.service;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.usecase.reservation.input.ReserveSeatCommand;

public interface CreateReservationService {
	CreateReservationResult processCreateReservation(ReserveSeatCommand command) throws CustomException;
}
