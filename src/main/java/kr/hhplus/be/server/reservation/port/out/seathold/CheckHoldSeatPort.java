package kr.hhplus.be.server.reservation.port.out.seathold;

import java.util.UUID;

public interface CheckHoldSeatPort {
    boolean checkHoldSeat(UUID seatId);
}
