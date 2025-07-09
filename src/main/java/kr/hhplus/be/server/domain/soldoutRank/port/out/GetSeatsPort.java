package kr.hhplus.be.server.domain.soldoutRank.port.out;

import kr.hhplus.be.server.domain.seat.domain.Seat;

import java.util.List;
import java.util.UUID;

public interface GetSeatsPort {
    List<Seat> getSeats(UUID concertDateId);
}
