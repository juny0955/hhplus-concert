package kr.hhplus.be.server.reservation.api.dto.request;

import java.util.UUID;

public record ReservationRequest(
	UUID concertId,
	UUID concertDateId
) {
}
