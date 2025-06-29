package kr.hhplus.be.server.domain.concert;

import java.util.UUID;

public interface ConcertRepository {
	Concert save(Concert concert);
	boolean existsById(UUID concertId);
	void deleteAll();
}
