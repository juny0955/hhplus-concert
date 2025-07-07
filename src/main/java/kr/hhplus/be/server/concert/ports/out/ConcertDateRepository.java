package kr.hhplus.be.server.concert.ports.out;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.concert.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.concert.domain.concertDate.ConcertDates;

public interface ConcertDateRepository {
	Optional<ConcertDate> findById(UUID concertDateId);

	ConcertDates findAvailableDatesWithAvailableSeatCount(UUID concertId);

	ConcertDate save(ConcertDate concertDate);

	boolean existsById(UUID concertDateId);

	void deleteAll();
}
