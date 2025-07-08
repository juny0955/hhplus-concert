package kr.hhplus.be.server.application.concert.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.exception.CustomException;

public interface GetConcertPort {
	Concert getConcert(UUID concertId) throws CustomException;
	void existsConcert(UUID concertId) throws CustomException;
}
