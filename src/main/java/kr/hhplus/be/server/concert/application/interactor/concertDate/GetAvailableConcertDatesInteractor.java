package kr.hhplus.be.server.concert.application.interactor.concertDate;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.application.service.ConcertDateApplicationService;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.ports.in.concertDate.GetAvailableConcertDatesInput;
import kr.hhplus.be.server.common.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetAvailableConcertDatesInteractor implements GetAvailableConcertDatesInput {

	private final ConcertDateApplicationService concertDateApplicationService;

	@Override
	public List<ConcertDate> getAvailableConcertDates(UUID concertId) throws CustomException {
		return concertDateApplicationService.getAvailableConcertDates(concertId);
	}
}
