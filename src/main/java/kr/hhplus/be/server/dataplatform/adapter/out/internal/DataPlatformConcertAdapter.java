package kr.hhplus.be.server.dataplatform.adapter.out.internal;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.port.in.concert.GetConcertByConcertDateIdUseCase;
import kr.hhplus.be.server.dataplatform.port.out.ConcertQueryPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataPlatformConcertAdapter implements ConcertQueryPort {

	private final GetConcertByConcertDateIdUseCase getConcertByConcertDateIdUseCase;

	@Override
	public Concert getConcertByConcertDateId(UUID concertDateId) throws CustomException {
		return getConcertByConcertDateIdUseCase.getConcert(concertDateId);
	}
}
