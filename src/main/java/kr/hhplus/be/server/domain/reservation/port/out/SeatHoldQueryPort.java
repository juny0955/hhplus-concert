package kr.hhplus.be.server.domain.reservation.port.out;

import kr.hhplus.be.server.common.exception.CustomException;

import java.util.UUID;

public interface SeatHoldQueryPort {
    void holdSeat(UUID seatId, UUID userId) throws CustomException;
    boolean checkHoldSeat(UUID seatId);
}
