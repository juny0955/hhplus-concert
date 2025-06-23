package kr.hhplus.be.server.domain.event.reservation;

import kr.hhplus.be.server.domain.event.Event;
import kr.hhplus.be.server.domain.event.EventTopic;
import kr.hhplus.be.server.usecase.reservation.service.CreateReservationResult;
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

    public static ReservationExpiredEvent from(CreateReservationResult createReservationResult) {
        return ReservationExpiredEvent.builder()
                .reservationId(createReservationResult.reservation().id())
                .paymentId(createReservationResult.payment().id())
                .seatId(createReservationResult.seat().id())
                .userId(createReservationResult.userId())
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
