package kr.hhplus.be.server.application.concert.port.out;

import java.util.UUID;

import kr.hhplus.be.server.exception.CustomException;

public interface ExistsConcertPort {
	void existsConcert(UUID concertId) throws CustomException;
}
