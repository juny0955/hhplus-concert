package kr.hhplus.be.server.domain.seatHold.port.out;

import java.util.UUID;

public interface HoldSeatPort {
    void holdSeat(UUID seatId, UUID userId);
}
