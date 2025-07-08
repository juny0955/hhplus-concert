package kr.hhplus.be.server.adapters.in.web.reservation.request;

import java.util.UUID;

public record ReservationRequest(
	UUID concertId,
	UUID concertDateId
) {
}
