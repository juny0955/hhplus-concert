package kr.hhplus.be.server.domain.concertDate.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concertDate.service.ConcertDateService;
import kr.hhplus.be.server.domain.concertDate.port.in.ValidDeadLineUseCase;
import kr.hhplus.be.server.common.exception.CustomException;
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
