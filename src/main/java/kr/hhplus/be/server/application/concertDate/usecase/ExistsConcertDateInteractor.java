package kr.hhplus.be.server.application.concertDate.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concertDate.service.ConcertDateService;
import kr.hhplus.be.server.application.concertDate.port.in.ExistsConcertDateInput;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExistsConcertDateInteractor implements ExistsConcertDateInput {

	private final ConcertDateService concertDateService;

	@Override
	public void existsConcertDate(UUID concertDateId) throws CustomException {
		concertDateService.existsConcertDate(concertDateId);
	}
}
