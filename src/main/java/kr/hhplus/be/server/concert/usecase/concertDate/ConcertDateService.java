package kr.hhplus.be.server.concert.usecase.concertDate;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.port.in.concertDate.GetAvailableConcertDatesUseCase;
import kr.hhplus.be.server.concert.port.out.concert.GetConcertPort;
import kr.hhplus.be.server.concert.port.out.concertDate.GetConcertDatePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConcertDateService implements GetAvailableConcertDatesUseCase {

	private final GetConcertDatePort getConcertDatePort;
	private final GetConcertPort getConcertPort;

	@Override
	public List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException {
		getConcertPort.existsConcert(concertId);
		return getConcertDatePort.getAvailableDatesWithSeatCount(concertId).concertDates();
	}
}
