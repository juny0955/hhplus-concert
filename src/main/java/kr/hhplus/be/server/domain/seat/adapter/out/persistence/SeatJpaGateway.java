package kr.hhplus.be.server.domain.seat.adapter.out.persistence;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.seat.domain.Seats;
import kr.hhplus.be.server.domain.seat.port.out.GetSeatPort;
import kr.hhplus.be.server.domain.seat.port.out.SaveSeatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatJpaGateway implements GetSeatPort, SaveSeatPort {

	private final JpaSeatRepository jpaSeatRepository;

	@Override
	public Seats getAvailableSeat(UUID concertId, UUID concertDateId) {
		List<Seat> results = jpaSeatRepository.findAvailableSeats(concertId.toString(), concertDateId.toString()).stream()
				.map(SeatEntity::toDomain)
				.toList();

		return new Seats(results);
	}

	@Override
	public List<Seat> getSeatsByConcertDateId(UUID concertDateId) {
		return jpaSeatRepository.findByConcertDateId(concertDateId.toString()).stream()
				.map(SeatEntity::toDomain)
				.toList();
	}

	@Override
	public Seat getSeat(UUID seatId) throws CustomException {
		return jpaSeatRepository.findById(seatId.toString())
				.map(SeatEntity::toDomain)
				.orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
	}

	@Override
	public Seat saveSeat(Seat seat) {
		SeatEntity seatEntity = jpaSeatRepository.save(SeatEntity.from(seat));
		return seatEntity.toDomain();
	}
}

