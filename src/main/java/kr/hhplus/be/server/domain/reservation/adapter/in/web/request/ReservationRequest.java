package kr.hhplus.be.server.domain.reservation.adapter.in.web.request;

import java.util.UUID;

public record ReservationRequest(
	UUID concertId,
	UUID concertDateId
) {
}
