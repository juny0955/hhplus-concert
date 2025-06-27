package kr.hhplus.be.server.domain.concertDate;

import java.util.Optional;
import java.util.UUID;

public interface ConcertDateRepository {
	Optional<ConcertDate> findById(UUID concertDateId);

	ConcertDates findAvailableDatesWithAvailableSeatCount(UUID concertId);

	ConcertDate save(ConcertDate concertDate);

	boolean existsById(UUID concertDateId);

	void deleteAll();
}
