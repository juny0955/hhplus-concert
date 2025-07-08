package kr.hhplus.be.server.domain.reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import lombok.Builder;

@Builder
public record ReservationCompletedData(
    UUID reservationId,
    UUID userId,
    UUID seatId,
    UUID concertDateId,
    BigDecimal amount,
    LocalDateTime completedAt,
    String paymentStatus
) {
    
    public static ReservationCompletedData from(PaymentSuccessEvent event) {
        return ReservationCompletedData.builder()
            .reservationId(event.reservation().id())
            .userId(event.user().id())
            .seatId(event.seat().id())
            .concertDateId(event.seat().concertDateId())
            .amount(event.payment().amount())
            .completedAt(event.occurredAt())
            .paymentStatus(event.payment().status().name())
            .build();
    }
}