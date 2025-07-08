package kr.hhplus.be.server.application.reservation.port.out;

import java.util.UUID;

import kr.hhplus.be.server.exception.CustomException;

public interface ValidDeadLinePort {
	void validDeadLine(UUID concertDateId) throws CustomException;
}
