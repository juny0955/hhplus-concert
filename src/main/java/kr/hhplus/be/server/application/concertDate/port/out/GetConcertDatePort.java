package kr.hhplus.be.server.application.concertDate.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.concertDate.ConcertDates;
import kr.hhplus.be.server.exception.CustomException;

public interface GetConcertDatePort {
	ConcertDates getAvailableDatesWithSeatCount(UUID concertId);
	void existsConcertDate(UUID concertDateId) throws CustomException;
}
