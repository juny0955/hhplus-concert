package kr.hhplus.be.server.reservation.port.in.seathold;

import kr.hhplus.be.server.common.exception.CustomException;

import java.util.UUID;

public interface HasHoldSeatUseCase {
    void hasHoldSeat(UUID seatId, UUID userId) throws CustomException;
}
