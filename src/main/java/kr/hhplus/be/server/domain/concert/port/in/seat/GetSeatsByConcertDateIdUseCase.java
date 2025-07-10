package kr.hhplus.be.server.domain.concert.port.in.seat;

import kr.hhplus.be.server.domain.concert.domain.seat.Seat;

import java.util.List;
import java.util.UUID;

public interface GetSeatsByConcertDateIdUseCase {
    List<Seat> getSeatsByConcertDateId(UUID concertId);
}
