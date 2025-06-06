package kr.hhplus.be.server.usecase.concert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.concert.ConcertDate;

public interface ConcertDateRepository {
	Optional<ConcertDate> findById(UUID concertDateId);

	List<ConcertDate> findAvailableDates(UUID concertId);

	Optional<ConcertDate> findAvailableDate(UUID concertId, UUID concertDateId);
}
