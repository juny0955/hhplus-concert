package kr.hhplus.be.server.domain.concert.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.port.out.GetConcertPort;
import kr.hhplus.be.server.common.exception.CustomException;
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
