package kr.hhplus.be.server.concert.port.in.concertDate;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface ValidDeadLineUseCase {
	void validDeadLine(UUID concertDateId) throws CustomException;
}
