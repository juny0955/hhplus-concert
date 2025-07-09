package kr.hhplus.be.server.domain.concertDate.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.concertDate.domain.ConcertDates;
import kr.hhplus.be.server.common.exception.CustomException;

public interface GetConcertDatePort {
	ConcertDates getAvailableDatesWithSeatCount(UUID concertId);
	void existsConcertDate(UUID concertDateId) throws CustomException;
}
