package kr.hhplus.be.server.domain.concert.port.in.concert;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface ValidOpenConcertUseCase {
	void validOpenConcert(UUID concertId) throws CustomException;
}
