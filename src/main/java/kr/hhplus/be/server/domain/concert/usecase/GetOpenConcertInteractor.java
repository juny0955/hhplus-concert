package kr.hhplus.be.server.domain.concert.usecase;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.service.ConcertService;
import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.concert.port.in.GetOpenConcertInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetOpenConcertInteractor implements GetOpenConcertInput {

	private final ConcertService concertService;

	@Override
	public List<Concert> getOpenConcert() {
		return concertService.getOpenConcerts();
	}
}
