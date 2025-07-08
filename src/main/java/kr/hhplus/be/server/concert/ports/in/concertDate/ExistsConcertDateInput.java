package kr.hhplus.be.server.concert.ports.in.concertDate;

import java.util.UUID;

import kr.hhplus.be.server.common.framework.exception.CustomException;

public interface ExistsConcertDateInput {
	void existsConcertDate(UUID concertDateId) throws CustomException;
}
