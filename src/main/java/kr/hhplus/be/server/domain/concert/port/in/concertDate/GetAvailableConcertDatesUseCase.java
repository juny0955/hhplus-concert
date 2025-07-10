package kr.hhplus.be.server.domain.concert.port.in.concertDate;

import java.util.List;
import java.util.UUID;

import kr.hhplus.be.server.domain.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.common.exception.CustomException;

public interface GetAvailableConcertDatesUseCase {
	List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException;
}
