package kr.hhplus.be.server.domain.seat.port.out;

import java.util.List;
import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.seat.domain.Seats;

public interface GetSeatPort {
	Seats getAvailableSeat(UUID concertId, UUID concertDateId);
    List<Seat> getSeatsByConcertDateId(UUID concertDateId);
    Seat getSeat(UUID seatId) throws CustomException;
}
