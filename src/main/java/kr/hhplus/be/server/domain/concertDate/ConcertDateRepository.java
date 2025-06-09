package kr.hhplus.be.server.domain.concertDate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConcertDateRepository {
	Optional<ConcertDate> findById(UUID concertDateId);

	List<ConcertDate> findAvailableDates(UUID concertId);

	Optional<ConcertDate> findAvailableDate(UUID concertId, UUID concertDateId);

	ConcertDate save(ConcertDate concertDate);
}
