package kr.hhplus.be.server.concert.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.concert.domain.concert.Concert;

public interface ConcertRepository {
	Concert save(Concert concert);
	Optional<Concert> findById(UUID concertId);
	boolean existsById(UUID concertId);
	void deleteAll();

	List<Concert> findByOpenConcerts();
}
