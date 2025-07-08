package kr.hhplus.be.server.application.concertDate.port.out;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.concertDate.ConcertDates;

public interface ConcertDateRepository {
	Optional<ConcertDate> findById(UUID concertDateId);

	ConcertDates findAvailableDatesWithAvailableSeatCount(UUID concertId);

	ConcertDate save(ConcertDate concertDate);

	boolean existsById(UUID concertDateId);

	void deleteAll();
}
