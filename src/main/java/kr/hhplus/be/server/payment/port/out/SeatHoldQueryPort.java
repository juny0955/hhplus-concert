package kr.hhplus.be.server.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface SeatHoldQueryPort {
    void hasSeatHold(UUID seatId, UUID userId) throws CustomException;
}
