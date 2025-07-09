package kr.hhplus.be.server.domain.concert.usecase;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.concert.port.in.GetOpenConcertUseCase;
import kr.hhplus.be.server.domain.concert.port.out.GetConcertPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetOpenConcertService implements GetOpenConcertUseCase {

	private final GetConcertPort getConcertPort;

	@Override
	public List<Concert> getOpenConcert() {
		return getConcertPort.getOpenConcerts();
	}
}
