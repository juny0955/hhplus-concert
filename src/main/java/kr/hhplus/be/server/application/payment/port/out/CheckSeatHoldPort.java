package kr.hhplus.be.server.application.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.exception.CustomException;

public interface CheckSeatHoldPort {
	void checkSeatHold(UUID seatId, UUID userId) throws CustomException;
}
