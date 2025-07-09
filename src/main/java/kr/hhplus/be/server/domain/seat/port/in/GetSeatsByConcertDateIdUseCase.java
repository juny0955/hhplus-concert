package kr.hhplus.be.server.domain.seat.port.in;

import kr.hhplus.be.server.domain.seat.domain.Seat;

import java.util.List;
import java.util.UUID;

public interface GetSeatsByConcertDateIdUseCase {
    List<Seat> getSeatsByConcertDateId(UUID concertId);
}
