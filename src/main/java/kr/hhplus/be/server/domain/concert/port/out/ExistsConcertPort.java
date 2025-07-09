package kr.hhplus.be.server.domain.concert.port.out;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface ExistsConcertPort {
	void existsConcert(UUID concertId) throws CustomException;
}
