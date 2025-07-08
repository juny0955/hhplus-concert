package kr.hhplus.be.server.application.concertDate.port.in;

import java.util.UUID;

import kr.hhplus.be.server.exception.CustomException;

public interface ExistsConcertDateInput {
	void existsConcertDate(UUID concertDateId) throws CustomException;
}
