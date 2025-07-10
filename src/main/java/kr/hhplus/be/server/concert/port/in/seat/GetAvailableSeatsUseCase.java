package kr.hhplus.be.server.concert.port.in.seat;

import java.util.List;
import java.util.UUID;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.common.exception.CustomException;

public interface GetAvailableSeatsUseCase {
	List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) throws CustomException;
}
