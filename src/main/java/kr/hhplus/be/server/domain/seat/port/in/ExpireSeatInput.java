package kr.hhplus.be.server.domain.seat.port.in;

import java.util.UUID;

import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.common.exception.CustomException;

public interface ExpireSeatInput {
	Seat expireSeat(UUID seatId) throws CustomException;
}
