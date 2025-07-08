package kr.hhplus.be.server.adapters.in.web.payment.request;

import java.util.UUID;

public record PayReservationRequest (
   UUID seatId
) {
}
