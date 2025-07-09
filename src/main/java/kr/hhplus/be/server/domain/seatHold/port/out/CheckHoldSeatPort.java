package kr.hhplus.be.server.domain.seatHold.port.out;

import java.util.UUID;

public interface CheckHoldSeatPort {
    boolean checkHoldSeat(UUID seatId);
}
