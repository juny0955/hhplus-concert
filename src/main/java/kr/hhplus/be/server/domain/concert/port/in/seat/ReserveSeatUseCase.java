package kr.hhplus.be.server.domain.concert.port.in.seat;

import java.util.UUID;

import kr.hhplus.be.server.domain.concert.domain.seat.Seat;
import kr.hhplus.be.server.common.exception.CustomException;

public interface ReserveSeatUseCase {
	Seat reserveSeat(UUID seatId) throws CustomException;
}
