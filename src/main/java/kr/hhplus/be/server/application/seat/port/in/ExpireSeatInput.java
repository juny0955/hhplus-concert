package kr.hhplus.be.server.application.seat.port.in;

import java.util.UUID;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.exception.CustomException;

public interface ExpireSeatInput {
	Seat expireSeat(UUID seatId) throws CustomException;
}
