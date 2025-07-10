package kr.hhplus.be.server.concert.port.out.seat;

import java.util.List;
import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.Seats;

public interface GetSeatPort {
	Seats getAvailableSeat(UUID concertId, UUID concertDateId);
    List<Seat> getSeatsByConcertDateId(UUID concertDateId);
    Seat getSeat(UUID seatId) throws CustomException;
}
