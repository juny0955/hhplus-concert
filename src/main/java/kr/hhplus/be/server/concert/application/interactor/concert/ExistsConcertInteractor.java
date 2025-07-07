package kr.hhplus.be.server.concert.application.interactor.concert;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.application.service.ConcertApplicationService;
import kr.hhplus.be.server.concert.ports.in.concert.ExistsConcertInput;
import kr.hhplus.be.server.framework.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExistsConcertInteractor implements ExistsConcertInput {

	private final ConcertApplicationService concertApplicationService;

	@Override
	public void existsConcert(UUID concertId) throws CustomException {
		concertApplicationService.existsConcert(concertId);
	}
}
