package kr.hhplus.be.server.application.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.exception.CustomException;

public interface ReleaseSeatHoldPort {
	void releaseSeatHold(UUID seatId, UUID userId) throws CustomException;
}
