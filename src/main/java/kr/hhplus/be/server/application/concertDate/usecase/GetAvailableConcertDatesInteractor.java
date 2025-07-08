package kr.hhplus.be.server.application.concertDate.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concertDate.port.in.GetAvailableConcertDatesInput;
import kr.hhplus.be.server.application.concertDate.service.ConcertDateService;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetAvailableConcertDatesInteractor implements GetAvailableConcertDatesInput {

	private final ConcertDateService concertDateService;

	@Override
	public List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException {
		return concertDateService.getAvailableConcertDates(concertId);
	}
}
