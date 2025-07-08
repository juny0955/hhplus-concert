package kr.hhplus.be.server.application.seatHold.port.in;

import java.util.UUID;

import kr.hhplus.be.server.exception.CustomException;

public interface ReleaseSeatHoldUseCase {
    void releaseSeatHold(UUID seatId, UUID userId) throws CustomException;
}
