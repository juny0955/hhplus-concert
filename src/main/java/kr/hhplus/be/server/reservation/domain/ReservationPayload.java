package kr.hhplus.be.server.reservation.domain;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ReservationPayload (
    UUID paymentId,
    UUID reservationId,
    UUID userId,
    UUID concertId,
    int seatNo,
    BigDecimal seatPrice
) {
    public static ReservationPayload from(ReservationCreatedEvent event) {
        return ReservationPayload.builder()
                .paymentId(event.payment().id())
                .reservationId(event.reservation().id())
                .userId(event.userId())
                .concertId(event.concertId())
                .seatNo(event.seat().seatNo())
                .seatPrice(event.seat().price())
                .build();
    }
}
