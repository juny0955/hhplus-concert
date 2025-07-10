package kr.hhplus.be.server.domain.concert.port.out.seat;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.seat.Seat;

public interface SaveSeatPort {
    Seat saveSeat(Seat seat) throws CustomException;
}
