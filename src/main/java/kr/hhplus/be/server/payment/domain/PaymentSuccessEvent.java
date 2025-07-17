package kr.hhplus.be.server.payment.domain;

import java.util.UUID;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.user.domain.User;
import lombok.Builder;

@Builder
public record PaymentSuccessEvent(
	UUID paymentId,
	UUID reservationId,
	UUID seatId,
	UUID userId
) {
	public static PaymentSuccessEvent from(Payment payment, Seat seat, Reservation reservation, User user) {
		return PaymentSuccessEvent.builder()
			.paymentId(payment.id())
			.reservationId(reservation.id())
			.seatId(seat.id())
			.userId(user.id())
			.build();
	}
}
