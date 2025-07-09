package kr.hhplus.be.server.domain.concert.port.out;

import java.util.List;
import java.util.UUID;

import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.common.exception.CustomException;

public interface GetConcertPort {
	Concert getConcert(UUID concertId) throws CustomException;
	List<Concert> getOpenConcerts();
	void existsConcert(UUID concertId) throws CustomException;
	Concert getConcertByConcertDateId(UUID concertDateId) throws CustomException;
}
