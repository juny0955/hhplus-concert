package kr.hhplus.be.server.domain.concert.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.common.exception.CustomException;

public interface GetConcertPort {
	Concert getConcert(UUID concertId) throws CustomException;
	void existsConcert(UUID concertId) throws CustomException;
}
