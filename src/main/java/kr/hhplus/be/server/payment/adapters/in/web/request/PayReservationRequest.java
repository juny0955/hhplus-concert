package kr.hhplus.be.server.payment.adapters.in.web.request;

import java.util.UUID;

public record PayReservationRequest (
   UUID seatId
) {
}
