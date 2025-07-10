package kr.hhplus.be.server.domain.concert.port.out.seathold;

import java.util.UUID;

public interface ReleaseSeatHoldPort {
    void releaseSeatHold(UUID seatId, UUID userId);
}
