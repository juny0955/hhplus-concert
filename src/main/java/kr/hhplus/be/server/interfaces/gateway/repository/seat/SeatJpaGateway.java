package kr.hhplus.be.server.interfaces.gateway.repository.seat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.usecase.concert.SeatRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatJpaGateway implements SeatRepository {

	private final JpaSeatRepository jpaSeatRepository;

	@Override
	public Seat save(Seat seat) {
		SeatEntity seatEntity = SeatEntity.from(seat);
		return jpaSeatRepository.save(seatEntity).toDomain();
	}

	@Override
	public Optional<Seat> findBySeatIdAndConcertDateId(UUID seatId, UUID concertDateId) {
		return jpaSeatRepository.findBySeatIdAndConcertDateId(seatId.toString(), concertDateId.toString())
			.map(SeatEntity::toDomain);
	}

	@Override
	public Integer countRemainingSeat(UUID concertDateId) {
		return jpaSeatRepository.countRemainingSeat(concertDateId.toString());
	}

	@Override
	public List<Seat> findAvailableSeats(UUID concertDateId) {
		return jpaSeatRepository.findAvailableSeats(concertDateId.toString()).stream()
			.map(SeatEntity::toDomain)
			.toList();
	}

	@Override
	public Optional<Seat> findById(UUID seatId) {
		return jpaSeatRepository.findById(seatId.toString())
			.map(SeatEntity::toDomain);
	}
}

