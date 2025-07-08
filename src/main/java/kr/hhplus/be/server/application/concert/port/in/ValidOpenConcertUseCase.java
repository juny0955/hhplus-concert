package kr.hhplus.be.server.application.concert.port.in;

import java.util.UUID;

import kr.hhplus.be.server.exception.CustomException;

public interface ValidOpenConcertUseCase {
	void validOpenConcert(UUID concertId) throws CustomException;
}
