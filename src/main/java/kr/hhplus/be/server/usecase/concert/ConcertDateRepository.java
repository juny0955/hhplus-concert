package kr.hhplus.be.server.usecase.concert;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.concert.ConcertDate;

public interface ConcertDateRepository {
	Optional<ConcertDate> findById(UUID concertDateId);
}
