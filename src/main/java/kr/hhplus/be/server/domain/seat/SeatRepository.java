package kr.hhplus.be.server.domain.seat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository {

	Seat save(Seat seat);
	List<Seat> findAvailableSeats(UUID concertId, UUID concertDateId);

	Optional<Seat> findById(UUID seatId);

	void deleteAll();

	int updateStatusReserved(UUID seatId);

	Optional<Seat> findBySeatIdAndConcertDateIdForUpdate(UUID seatId, UUID concertDateId);
}
