package kr.hhplus.be.server.usecase.concert;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.concert.Concert;

public interface ConcertRepository {
	boolean existsConcert(UUID concertId);

	Optional<Concert> findById(UUID concertId);
}
