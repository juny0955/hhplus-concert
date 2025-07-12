package kr.hhplus.be.server.reservation.port.out;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.Seat;

public interface SeatQueryPort {
    Seat reserveSeat(UUID seatId, UUID concertId, UUID concertDateId) throws CustomException;
    Seat expireSeat(UUID seatId) throws CustomException;
}
