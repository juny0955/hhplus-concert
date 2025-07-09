package kr.hhplus.be.server.domain.concertDate.port.in;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface ValidDeadLineUseCase {
	void validDeadLine(UUID concertDateId) throws CustomException;
}
