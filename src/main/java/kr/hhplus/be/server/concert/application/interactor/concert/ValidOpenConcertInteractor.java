package kr.hhplus.be.server.concert.application.interactor.concert;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.application.service.ConcertApplicationService;
import kr.hhplus.be.server.concert.ports.in.concert.ValidOpenConcertInput;
import kr.hhplus.be.server.common.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidOpenConcertInteractor implements ValidOpenConcertInput {

	private final ConcertApplicationService concertApplicationService;

	@Override
	public void validOpenConcert(UUID concertId) throws CustomException {
		concertApplicationService.validOpenConcert(concertId);
	}
}
