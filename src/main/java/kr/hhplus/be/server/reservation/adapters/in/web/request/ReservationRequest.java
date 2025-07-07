package kr.hhplus.be.server.reservation.adapters.in.web.request;

import java.util.UUID;

public record ReservationRequest(
	UUID concertId,
	UUID concertDateId
) {
}
