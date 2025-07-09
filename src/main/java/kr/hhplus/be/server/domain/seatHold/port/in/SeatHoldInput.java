package kr.hhplus.be.server.domain.seatHold.port.in;

import kr.hhplus.be.server.common.exception.CustomException;

import java.util.UUID;

public interface SeatHoldInput {
    void seatHold(UUID seatId, UUID userId) throws CustomException;
}
