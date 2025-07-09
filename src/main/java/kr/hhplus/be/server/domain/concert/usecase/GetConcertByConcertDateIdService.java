package kr.hhplus.be.server.domain.concert.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.concert.port.in.GetConcertByConcertDateIdUseCase;
import kr.hhplus.be.server.domain.concert.port.out.GetConcertPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetConcertByConcertDateIdService implements GetConcertByConcertDateIdUseCase {

	private final GetConcertPort getConcertPort;

	@Override
	public Concert getConcert(UUID concertDateId) throws CustomException {
		return getConcertPort.getConcertByConcertDateId(concertDateId);
	}
}
