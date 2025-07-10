package kr.hhplus.be.server.domain.reservation.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.seat.Seat;

import java.util.UUID;

public interface SeatQueryPort {
    Seat reserveSeat(UUID seatId) throws CustomException;
    Seat expireSeat(UUID seatId) throws CustomException;
}
