package kr.hhplus.be.server.queue.port.out;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface ConcertQueryPort {
	void existsConcert(UUID concertId) throws CustomException;
}
