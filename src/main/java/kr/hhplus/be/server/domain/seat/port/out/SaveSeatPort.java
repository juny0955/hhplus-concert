package kr.hhplus.be.server.domain.seat.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.seat.domain.Seat;

public interface SaveSeatPort {
    Seat saveSeat(Seat seat) throws CustomException;
}
