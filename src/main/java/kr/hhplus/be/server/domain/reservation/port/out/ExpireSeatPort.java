package kr.hhplus.be.server.domain.reservation.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.seat.domain.Seat;

import java.util.UUID;

public interface ExpireSeatPort {
    Seat expireSeat(UUID seatId) throws CustomException;
}
