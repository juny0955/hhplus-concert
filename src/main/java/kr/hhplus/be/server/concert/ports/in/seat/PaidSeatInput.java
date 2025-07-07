package kr.hhplus.be.server.concert.ports.in.seat;

import java.util.UUID;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.framework.exception.CustomException;

public interface PaidSeatInput {
	Seat paidSeat(UUID seatId) throws CustomException;
}
