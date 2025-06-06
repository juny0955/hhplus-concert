package kr.hhplus.be.server.usecase.concert;

import java.util.UUID;

public interface ConcertRepository {
	boolean existsConcert(UUID concertId);
}
