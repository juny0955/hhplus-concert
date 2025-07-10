package kr.hhplus.be.server.domain.concert.port.out.concertDate;

import java.util.UUID;

import kr.hhplus.be.server.domain.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.concert.domain.concertDate.ConcertDates;
import kr.hhplus.be.server.common.exception.CustomException;

public interface GetConcertDatePort {
	ConcertDate getConcertDate(UUID concertDateId) throws CustomException;
	ConcertDates getAvailableDatesWithSeatCount(UUID concertId);
	void existsConcertDate(UUID concertDateId) throws CustomException;
}
