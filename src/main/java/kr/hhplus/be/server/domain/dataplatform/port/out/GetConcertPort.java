package kr.hhplus.be.server.domain.dataplatform.port.out;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.Concert;

public interface GetConcertPort {
	Concert getConcertByConcertDateId(UUID concertDateId) throws CustomException;
}
