package kr.hhplus.be.server.domain.seatHold.port.in;

import java.util.UUID;

public interface CheckHoldSeatUseCase {
    boolean checkHoldSeat(UUID seatId);
}
