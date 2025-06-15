package kr.hhplus.be.server.infrastructure.persistence.seat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatJpaGateway implements SeatRepository {

	private final JpaSeatRepository jpaSeatRepository;

	@Override
	public Seat save(Seat seat) {
		SeatEntity seatEntity = jpaSeatRepository.save(SeatEntity.from(seat));
		return seatEntity.toDomain();
	}

	@Override
	public Optional<Seat> findBySeatIdAndConcertDateIdWithLock(UUID seatId, UUID concertDateId) {
		return jpaSeatRepository.findBySeatIdAndConcertDateIdWithLock(seatId.toString(), concertDateId.toString())
			.map(SeatEntity::toDomain);
	}

	@Override
	public List<Seat> findAvailableSeats(UUID concertId, UUID concertDateId) {
		return jpaSeatRepository.findAvailableSeats(concertId.toString(), concertDateId.toString()).stream()
			.map(SeatEntity::toDomain)
			.toList();
	}

	@Override
	public Optional<Seat> findById(UUID seatId) {
		return jpaSeatRepository.findById(seatId.toString())
			.map(SeatEntity::toDomain);
	}

	@Override
	public void deleteAll() {
		jpaSeatRepository.deleteAll();
	}
}

