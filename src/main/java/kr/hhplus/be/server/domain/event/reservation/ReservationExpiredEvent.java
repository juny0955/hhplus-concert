package kr.hhplus.be.server.domain.event.reservation;

import kr.hhplus.be.server.domain.event.Event;
import kr.hhplus.be.server.domain.event.EventTopic;
import kr.hhplus.be.server.usecase.reservation.interactor.ReservationTransactionResult;
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

    public static ReservationExpiredEvent from(ReservationTransactionResult reservationTransactionResult) {
        return ReservationExpiredEvent.builder()
                .reservationId(reservationTransactionResult.reservation().id())
                .paymentId(reservationTransactionResult.payment().id())
                .seatId(reservationTransactionResult.seat().id())
                .userId(reservationTransactionResult.userId())
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
