package kr.hhplus.be.server.reservation.port.out.seathold;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface CheckHoldSeatPort {
    void checkHoldSeat(UUID seatId) throws CustomException;
}
