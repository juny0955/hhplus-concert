package kr.hhplus.be.server.application.seat.port.in;

import java.util.List;
import java.util.UUID;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.exception.CustomException;

public interface GetAvailableSeatsInput {
	List<Seat> getAvailableSeats(UUID concertId, UUID concertDateId) throws CustomException;
}
