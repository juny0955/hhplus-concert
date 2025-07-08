package kr.hhplus.be.server.application.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.exception.CustomException;

public interface PaidSeatPort {
	Seat paidSeat(UUID seatId) throws CustomException;
}
