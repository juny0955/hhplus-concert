package kr.hhplus.be.server.application.concertDate.port.in;

import java.util.List;
import java.util.UUID;

import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.exception.CustomException;

public interface GetAvailableConcertDatesUseCase {
	List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException;
}
