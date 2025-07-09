package kr.hhplus.be.server.domain.payment.adapter.in.web.request;

import java.util.UUID;

public record PayReservationRequest (
   UUID seatId
) {
}
