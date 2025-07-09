package kr.hhplus.be.server.domain.concert.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.concert.domain.Concert;

public interface ConcertRepository {
	Concert save(Concert concert);
	Optional<Concert> findById(UUID concertId);
	boolean existsById(UUID concertId);
	void deleteAll();

	List<Concert> findByOpenConcerts();
}
