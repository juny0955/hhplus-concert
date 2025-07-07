package kr.hhplus.be.server.concert.ports.in.concert;

import java.util.UUID;

import kr.hhplus.be.server.framework.exception.CustomException;

public interface ValidOpenConcertInput {
	void validOpenConcert(UUID concertId) throws CustomException;
}
