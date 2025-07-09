package kr.hhplus.be.server.domain.reservation.domain;

import kr.hhplus.be.server.domain.reservation.dto.ExpiredReservationResult;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReservationExpiredEvent (
    UUID reservationId,
    UUID paymentId,
    UUID seatId,
    UUID userId,
    LocalDateTime occurredAt
) {

    public static ReservationExpiredEvent from(ExpiredReservationResult expiredReservationResult) {
        return ReservationExpiredEvent.builder()
                .reservationId(expiredReservationResult.reservationId())
                .paymentId(expiredReservationResult.paymentId())
                .seatId(expiredReservationResult.seatId())
                .userId(expiredReservationResult.userId())
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
