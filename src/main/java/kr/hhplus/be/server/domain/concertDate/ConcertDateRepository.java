package kr.hhplus.be.server.domain.concertDate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConcertDateRepository {
	Optional<ConcertDate> findById(UUID concertDateId);

	List<ConcertDate> findAvailableDatesWithAvailableSeatCount(UUID concertId);

	ConcertDate save(ConcertDate concertDate);

	boolean existsById(UUID concertDateId);
}
