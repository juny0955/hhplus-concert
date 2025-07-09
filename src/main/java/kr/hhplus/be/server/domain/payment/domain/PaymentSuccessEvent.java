package kr.hhplus.be.server.domain.payment.domain;

import java.time.LocalDateTime;

import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.user.domain.User;
import lombok.Builder;

@Builder
public record PaymentSuccessEvent(
	Payment payment,
	Reservation reservation,
	Seat seat,
	User user,
	LocalDateTime occurredAt
) {
	public static PaymentSuccessEvent from(Payment payment, Seat seat, Reservation reservation, User user) {
		return PaymentSuccessEvent.builder()
			.payment(payment)
			.reservation(reservation)
			.seat(seat)
			.user(user)
			.occurredAt(LocalDateTime.now())
			.build();
	}
}
