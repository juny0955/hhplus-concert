package kr.hhplus.be.server.concert.usecase.concertDate;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.port.in.concertDate.GetAvailableConcertDatesUseCase;
import kr.hhplus.be.server.concert.port.in.concertDate.ValidDeadLineUseCase;
import kr.hhplus.be.server.concert.port.out.concert.GetConcertPort;
import kr.hhplus.be.server.concert.port.out.concertDate.GetConcertDatePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConcertDateService implements GetAvailableConcertDatesUseCase, ValidDeadLineUseCase {

	private final GetConcertDatePort getConcertDatePort;
	private final GetConcertPort getConcertPort;

	public void validDeadLine(UUID concertDateId) throws CustomException {
		ConcertDate concertDate = getConcertDatePort.getConcertDate(concertDateId);
		if (!concertDate.checkDeadline())
			throw new CustomException(ErrorCode.OVER_DEADLINE);
	}

	public List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException {
		getConcertPort.existsConcert(concertId);
		return getConcertDatePort.getAvailableDatesWithSeatCount(concertId).concertDates();
	}
}
