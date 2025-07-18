package kr.hhplus.be.server.concert.port.in.seat;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.reservation.domain.PaidReservationEvent;

public interface PaidSeatUseCase {
	void paidSeat(PaidReservationEvent event) throws CustomException;
}
