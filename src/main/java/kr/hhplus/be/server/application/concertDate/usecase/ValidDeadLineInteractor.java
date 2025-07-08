package kr.hhplus.be.server.application.concertDate.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concertDate.service.ConcertDateService;
import kr.hhplus.be.server.application.concertDate.port.in.ValidDeadLineInput;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidDeadLineInteractor implements ValidDeadLineInput {

	private final ConcertDateService concertDateService;

	@Override
	public void validDeadLine(UUID concertDateId) throws CustomException {
		concertDateService.validDeadLine(concertDateId);
	}
}
