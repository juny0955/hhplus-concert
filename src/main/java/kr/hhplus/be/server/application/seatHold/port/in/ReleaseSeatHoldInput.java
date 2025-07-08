package kr.hhplus.be.server.application.seatHold.port.in;

import kr.hhplus.be.server.exception.CustomException;

import java.util.UUID;

public interface ReleaseSeatHoldInput {
    void releaseSeatHold(UUID seatId, UUID userId) throws CustomException;
}
