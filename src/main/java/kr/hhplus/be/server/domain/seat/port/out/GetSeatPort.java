package kr.hhplus.be.server.domain.seat.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.seat.domain.Seats;

public interface GetSeatPort {
	Seats getAvailableSeat(UUID concertId, UUID concertDateId);
}
