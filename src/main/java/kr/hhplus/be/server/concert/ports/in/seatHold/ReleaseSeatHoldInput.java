package kr.hhplus.be.server.concert.ports.in.seatHold;

import kr.hhplus.be.server.common.framework.exception.CustomException;

import java.util.UUID;

public interface ReleaseSeatHoldInput {
    void releaseSeatHold(UUID seatId, UUID userId) throws CustomException;
}
