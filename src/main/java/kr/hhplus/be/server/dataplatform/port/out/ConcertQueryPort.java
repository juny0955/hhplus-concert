package kr.hhplus.be.server.dataplatform.port.out;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.concert.Concert;

public interface ConcertQueryPort {
	Concert getConcertByConcertDateId(UUID concertDateId) throws CustomException;
}
