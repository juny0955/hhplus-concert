package kr.hhplus.be.server.application.concert.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concert.port.out.GetConcertPort;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExistsConcertInteractor implements ExistsConcertInput {

	private final GetConcertPort getConcertPort;

	@Override
	public void existsConcert(UUID concertId) throws CustomException {
		getConcertPort.existsConcert(concertId);
	}
}
