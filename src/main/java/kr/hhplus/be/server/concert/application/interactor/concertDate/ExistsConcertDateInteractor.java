package kr.hhplus.be.server.concert.application.interactor.concertDate;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.application.service.ConcertDateApplicationService;
import kr.hhplus.be.server.concert.ports.in.concertDate.ExistsConcertDateInput;
import kr.hhplus.be.server.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExistsConcertDateInteractor implements ExistsConcertDateInput {

	private final ConcertDateApplicationService concertDateApplicationService;

	@Override
	public void existsConcertDate(UUID concertDateId) throws CustomException {
		concertDateApplicationService.existsConcertDate(concertDateId);
	}
}
