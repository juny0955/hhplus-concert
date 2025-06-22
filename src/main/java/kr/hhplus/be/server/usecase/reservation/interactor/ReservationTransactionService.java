package kr.hhplus.be.server.usecase.reservation.interactor;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.usecase.reservation.input.ReserveSeatCommand;

public interface ReservationTransactionService {
	ReservationTransactionResult processReservationTransaction(ReserveSeatCommand command) throws CustomException;
}
