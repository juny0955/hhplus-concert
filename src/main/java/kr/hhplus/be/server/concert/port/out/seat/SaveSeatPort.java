package kr.hhplus.be.server.concert.port.out.seat;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.Seat;

public interface SaveSeatPort {
    Seat saveSeat(Seat seat) throws CustomException;
}
