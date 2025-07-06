package kr.hhplus.be.server.reservation.domain;

import kr.hhplus.be.server.infrastructure.event.Event;
import kr.hhplus.be.server.infrastructure.event.EventTopic;
import kr.hhplus.be.server.infrastructure.persistence.reservation.ExpiredReservationResult;
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
) implements Event {

    public static ReservationExpiredEvent from(ExpiredReservationResult expiredReservationResult) {
        return ReservationExpiredEvent.builder()
                .reservationId(expiredReservationResult.reservationId())
                .paymentId(expiredReservationResult.paymentId())
                .seatId(expiredReservationResult.seatId())
                .userId(expiredReservationResult.userId())
                .occurredAt(LocalDateTime.now())
                .build();
    }

    @Override
    public EventTopic getTopic() {
        return EventTopic.RESERVATION_EXPIRED;
    }

    @Override
    public String getKey() {
        return reservationId.toString();
    }
}
