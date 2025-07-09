package kr.hhplus.be.server.domain.concertDate.port.out;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.concertDate.domain.ConcertDate;

public interface ConcertDateRepository {
	Optional<ConcertDate> findById(UUID concertDateId);

	ConcertDate save(ConcertDate concertDate);

	void deleteAll();
}
