package kr.hhplus.be.server.concert.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.Seats;

public interface SeatRepository {

	Seat save(Seat seat);
	Seats findAvailableSeats(UUID concertId, UUID concertDateId);
	Optional<Seat> findById(UUID seatId);
	Optional<Seat> findBySeatIdAndConcertDateId(UUID seatId, UUID concertDateId);

	void deleteAll();

	List<Seat> findByConcertDateId(UUID concertDateId);
}
