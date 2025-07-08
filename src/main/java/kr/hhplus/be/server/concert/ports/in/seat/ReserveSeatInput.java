package kr.hhplus.be.server.concert.ports.in.seat;

import java.util.UUID;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.common.framework.exception.CustomException;

public interface ReserveSeatInput {
	Seat reserveSeat(UUID seatId) throws CustomException;
}
