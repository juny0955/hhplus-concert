package kr.hhplus.be.server.application.concert.port.in;

import java.util.UUID;

import kr.hhplus.be.server.exception.CustomException;

public interface ExistsConcertInput {
	void existsConcert(UUID concertId) throws CustomException;
}
