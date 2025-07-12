package kr.hhplus.be.server.reservation.port.out.seathold;

import java.util.UUID;

public interface ReleaseSeatHoldPort {
    void releaseSeatHold(UUID seatId);
}
