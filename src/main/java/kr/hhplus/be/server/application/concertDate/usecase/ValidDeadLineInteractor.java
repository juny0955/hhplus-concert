package kr.hhplus.be.server.application.concertDate.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concertDate.service.ConcertDateService;
import kr.hhplus.be.server.application.concertDate.port.in.ValidDeadLineUseCase;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidDeadLineInteractor implements ValidDeadLineUseCase {

	private final ConcertDateService concertDateService;

	@Override
	public void validDeadLine(UUID concertDateId) throws CustomException {
		concertDateService.validDeadLine(concertDateId);
	}
}
