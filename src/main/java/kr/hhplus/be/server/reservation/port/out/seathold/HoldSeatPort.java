package kr.hhplus.be.server.reservation.port.out.seathold;

import java.util.UUID;

public interface HoldSeatPort {
    void holdSeat(UUID seatId, UUID userId);
}
