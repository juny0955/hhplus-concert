package kr.hhplus.be.server.domain.concertDate.port.in;

import java.util.List;
import java.util.UUID;

import kr.hhplus.be.server.domain.concertDate.domain.ConcertDate;
import kr.hhplus.be.server.common.exception.CustomException;

public interface GetAvailableConcertDatesUseCase {
	List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException;
}
