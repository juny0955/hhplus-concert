package kr.hhplus.be.server.domain.concert.port.out.seathold;

import java.util.UUID;

public interface CheckHoldSeatPort {
    boolean checkHoldSeat(UUID seatId);
}
