package kr.hhplus.be.server.reservation.port.in.reservation;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.PaidReservationFailEvent;
import kr.hhplus.be.server.user.domain.PaidUserEvent;

public interface PaidReservationUseCase {
	void paidReservation(PaidUserEvent event) throws CustomException;
	void paidReservationFail(PaidReservationFailEvent event) throws CustomException;
}
