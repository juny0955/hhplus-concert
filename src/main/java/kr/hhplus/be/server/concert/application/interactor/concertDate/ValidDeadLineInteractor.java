package kr.hhplus.be.server.concert.application.interactor.concertDate;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.application.service.ConcertDateApplicationService;
import kr.hhplus.be.server.concert.ports.in.concertDate.ValidDeadLineInput;
import kr.hhplus.be.server.common.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidDeadLineInteractor implements ValidDeadLineInput {

	private final ConcertDateApplicationService concertDateApplicationService;

	@Override
	public void validDeadLine(UUID concertDateId) throws CustomException {
		concertDateApplicationService.validDeadLine(concertDateId);
	}
}
