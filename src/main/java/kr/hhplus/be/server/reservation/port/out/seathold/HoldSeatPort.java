package kr.hhplus.be.server.reservation.port.out.seathold;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface HoldSeatPort {
    void holdSeat(UUID seatId, UUID userId) throws CustomException;
}
