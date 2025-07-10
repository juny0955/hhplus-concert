package kr.hhplus.be.server.domain.payment.port.out;

import kr.hhplus.be.server.common.exception.CustomException;

import java.util.UUID;

public interface SeatHoldQueryPort {
    void releaseSeatHold(UUID seatId, UUID userId) throws CustomException;
    void hasSeatHold(UUID seatId, UUID userId) throws CustomException;
}
