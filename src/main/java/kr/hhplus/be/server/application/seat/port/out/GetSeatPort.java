package kr.hhplus.be.server.application.seat.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.seat.Seats;

public interface GetSeatPort {
	Seats getAvailableSeat(UUID concertId, UUID concertDateId);
}
