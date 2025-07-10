package kr.hhplus.be.server.domain.concert.port.in.seathold;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface ReleaseSeatHoldUseCase {
    void releaseSeatHold(UUID seatId, UUID userId) throws CustomException;
}
