package kr.hhplus.be.server.domain.concert.port.in.seathold;

import java.util.UUID;

public interface CheckHoldSeatUseCase {
    boolean checkHoldSeat(UUID seatId);
}
