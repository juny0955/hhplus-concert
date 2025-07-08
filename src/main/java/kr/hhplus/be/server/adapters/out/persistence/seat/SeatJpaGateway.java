package kr.hhplus.be.server.adapters.out.persistence.seat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.seat.port.out.SeatRepository;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.Seats;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatJpaGateway implements SeatRepository {

	private final JpaSeatRepository jpaSeatRepository;

	@CacheEvict(value = "cache:seat:available", key   = "#seat.concertDateId")
	@Override
	public Seat save(Seat seat) {
		SeatEntity seatEntity = jpaSeatRepository.save(SeatEntity.from(seat));
		return seatEntity.toDomain();
	}

	@Override
	public Optional<Seat> findBySeatIdAndConcertDateId(UUID seatId, UUID concertDateId) {
		return jpaSeatRepository.findBySeatIdAndConcertDateId(seatId.toString(), concertDateId.toString())
			.map(SeatEntity::toDomain);
	}

	@Cacheable(value = "cache:seat:available", key = "#concertDateId")
	@Override
	public Seats findAvailableSeats(UUID concertId, UUID concertDateId) {
		List<Seat> results = jpaSeatRepository.findAvailableSeats(concertId.toString(), concertDateId.toString()).stream()
			.map(SeatEntity::toDomain)
			.toList();

		return new Seats(results);
	}

	@Override
	public Optional<Seat> findById(UUID seatId) {
		return jpaSeatRepository.findById(seatId.toString())
			.map(SeatEntity::toDomain);
	}

	@Override
	public List<Seat> findByConcertDateId(UUID concertDateId) {
		return jpaSeatRepository.findByConcertDateId(concertDateId.toString()).stream()
			.map(SeatEntity::toDomain)
			.toList();
	}

	@Override
	public void deleteAll() {
		jpaSeatRepository.deleteAll();
	}
}

