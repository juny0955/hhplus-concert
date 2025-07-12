package kr.hhplus.be.server.concert.port.in.seat;

import java.util.UUID;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.common.exception.CustomException;

public interface ExpireSeatUseCase {
	Seat expireSeat(UUID seatId) throws CustomException;
}
