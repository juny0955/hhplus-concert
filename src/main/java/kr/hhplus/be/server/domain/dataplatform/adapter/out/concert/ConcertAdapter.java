package kr.hhplus.be.server.domain.dataplatform.adapter.out.concert;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.concert.port.in.GetConcertByConcertDateIdUseCase;
import kr.hhplus.be.server.domain.dataplatform.port.out.GetConcertPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertAdapter implements GetConcertPort {

	private final GetConcertByConcertDateIdUseCase getConcertByConcertDateIdUseCase;

	@Override
	public Concert getConcertByConcertDateId(UUID concertDateId) throws CustomException {
		return getConcertByConcertDateIdUseCase.getConcert(concertDateId);
	}
}
