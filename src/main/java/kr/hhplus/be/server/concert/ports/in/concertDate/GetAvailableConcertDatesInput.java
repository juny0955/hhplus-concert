package kr.hhplus.be.server.concert.ports.in.concertDate;

import java.util.List;
import java.util.UUID;

import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.framework.exception.CustomException;

public interface GetAvailableConcertDatesInput {
	List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException;
}
