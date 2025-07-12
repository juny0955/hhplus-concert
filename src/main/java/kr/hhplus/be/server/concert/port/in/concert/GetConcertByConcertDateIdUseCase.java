package kr.hhplus.be.server.concert.port.in.concert;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.concert.Concert;

public interface GetConcertByConcertDateIdUseCase {
	Concert getConcert(UUID concertDateId) throws CustomException;
}
