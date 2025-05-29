package kr.hhplus.be.server.interfaces.dto.request;

import java.util.UUID;

public record ReservationRequest(
	UUID concertDateId,
	UUID seatId
) {
}
