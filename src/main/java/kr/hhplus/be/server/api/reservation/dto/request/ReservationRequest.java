package kr.hhplus.be.server.api.reservation.dto.request;

import java.util.UUID;

public record ReservationRequest(
	UUID concertId,
	UUID concertDateId
) {
}
