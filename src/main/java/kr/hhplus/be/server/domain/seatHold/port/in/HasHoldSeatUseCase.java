package kr.hhplus.be.server.domain.seatHold.port.in;

import kr.hhplus.be.server.common.exception.CustomException;

import java.util.UUID;

public interface HasHoldSeatUseCase {
    void hasHoldSeat(UUID seatId, UUID userId) throws CustomException;
}
