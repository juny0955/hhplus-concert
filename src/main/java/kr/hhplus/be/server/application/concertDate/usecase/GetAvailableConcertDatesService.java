package kr.hhplus.be.server.application.concertDate.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concert.port.out.GetConcertPort;
import kr.hhplus.be.server.application.concertDate.port.in.GetAvailableConcertDatesUseCase;
import kr.hhplus.be.server.application.concertDate.port.out.GetConcertDatePort;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetAvailableConcertDatesService implements GetAvailableConcertDatesUseCase {

	private final GetConcertPort getConcertPort;
	private final GetConcertDatePort getConcertDatePort;

	@Override
	public List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException {
		getConcertPort.existsConcert(concertId);
		return getConcertDatePort.getAvailableDatesWithSeatCount(concertId).concertDates();
	}
}
