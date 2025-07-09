package kr.hhplus.be.server.domain.concert.port.in;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.Concert;

public interface GetConcertByConcertDateIdUseCase {
	Concert getConcert(UUID concertDateId) throws CustomException;
}
