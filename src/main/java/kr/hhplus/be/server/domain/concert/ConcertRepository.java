package kr.hhplus.be.server.domain.concert;

import java.util.Optional;
import java.util.UUID;

public interface ConcertRepository {
	Concert save(Concert concert);
	Optional<Concert> findById(UUID concertId);
	boolean existsById(UUID concertId);
	void deleteAll();
}
