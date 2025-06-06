package kr.hhplus.be.server.usecase.concert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.concert.Seat;

public interface SeatRepository {

	Seat save(Seat seat);
	Optional<Seat> findBySeatIdAndConcertDateId(UUID seatId, UUID concertDateId);
	Integer countRemainingSeat(UUID concertDateId);
	List<Seat> findAvailableSeats(UUID concertDateId);

	Optional<Seat> findById(UUID seatId);
}
