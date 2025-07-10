package kr.hhplus.be.server.reservation.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.Seat;

import java.util.UUID;

public interface SeatQueryPort {
    Seat reserveSeat(UUID seatId) throws CustomException;
    Seat expireSeat(UUID seatId) throws CustomException;
}
