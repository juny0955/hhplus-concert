package kr.hhplus.be.server.domain.reservation.dto;

import java.util.UUID;

public record ExpiredReservationResult(
    UUID reservationId,
    UUID userId,
    UUID seatId,
    UUID paymentId
) {
    public static ExpiredReservationResult from(UUID reservationId, UUID paymentId, UUID seatId, UUID userId) {
        return new ExpiredReservationResult(
            reservationId,
            userId,
            seatId,
            paymentId
        );
    }
}