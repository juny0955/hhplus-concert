package kr.hhplus.be.server.domain.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface CheckSeatHoldPort {
	void checkSeatHold(UUID seatId, UUID userId) throws CustomException;
}
