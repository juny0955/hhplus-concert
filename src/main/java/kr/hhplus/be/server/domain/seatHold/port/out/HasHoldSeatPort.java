package kr.hhplus.be.server.domain.seatHold.port.out;

import kr.hhplus.be.server.common.exception.CustomException;

import java.util.UUID;

public interface HasHoldSeatPort {
    void hasHoldSeat(UUID seatId, UUID userId) throws CustomException;
}
