package kr.hhplus.be.server.domain.reservation.port.out;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface ValidDeadLinePort {
	void validDeadLine(UUID concertDateId) throws CustomException;
}
