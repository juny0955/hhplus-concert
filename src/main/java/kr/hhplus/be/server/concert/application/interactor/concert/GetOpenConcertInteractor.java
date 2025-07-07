package kr.hhplus.be.server.concert.application.interactor.concert;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.application.service.concert.ConcertApplicationService;
import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.ports.in.concert.GetOpenConcertInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetOpenConcertInteractor implements GetOpenConcertInput {

	private final ConcertApplicationService concertApplicationService;

	@Override
	public List<Concert> getOpenConcert() {
		return concertApplicationService.getOpenConcerts();
	}
}
