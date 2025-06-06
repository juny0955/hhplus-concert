package kr.hhplus.be.server.interfaces.api.concert.dto.request;

import java.util.UUID;

public record ReservationRequest(
	UUID seatId,
	UUID concertDateId
) {
}
