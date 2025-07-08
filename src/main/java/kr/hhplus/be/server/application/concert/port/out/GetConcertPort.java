package kr.hhplus.be.server.application.concert.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.concert.Concert;

public interface GetConcertPort {
	Concert getConcert(UUID concertId);
}
