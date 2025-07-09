package kr.hhplus.be.server.domain.seatHold.port.out;

import java.util.UUID;

public interface ReleaseSeatHoldPort {
    void releaseSeatHold(UUID seatId, UUID userId);
}
