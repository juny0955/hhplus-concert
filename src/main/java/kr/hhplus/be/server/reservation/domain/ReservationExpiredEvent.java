package kr.hhplus.be.server.reservation.domain;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReservationExpiredEvent (
        Reservation reservation,
        Payment payment,
        Seat seat,
        LocalDateTime occurredAt
) {
    public static ReservationExpiredEvent from(Reservation reservation, Payment payment, Seat seat) {
        return ReservationExpiredEvent.builder()
                .reservation(reservation)
                .payment(payment)
                .seat(seat)
                .occurredAt(reservation.updatedAt())
                .build();
    }
}
