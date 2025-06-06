package kr.hhplus.be.server.usecase.concert;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.concert.Seat;

public interface SeatRepository {

	Seat save(Seat seat);
	Optional<Seat> findBySeatIdAndConcertDateId(UUID seatId, UUID concertDateId);
}
