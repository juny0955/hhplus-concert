package kr.hhplus.be.server.domain.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.common.exception.CustomException;

public interface PaidSeatPort {
	Seat paidSeat(UUID seatId) throws CustomException;
}
