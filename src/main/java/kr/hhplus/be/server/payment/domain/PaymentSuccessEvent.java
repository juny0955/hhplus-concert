package kr.hhplus.be.server.payment.domain;

import java.time.LocalDateTime;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.payment.application.dto.PaymentResult;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.user.domain.User;
import lombok.Builder;

@Builder
public record PaymentSuccessEvent(
	Payment payment,
	Reservation reservation,
	Seat seat,
	User user,
	LocalDateTime occurredAt
) {

	public static PaymentSuccessEvent from(PaymentResult paymentResult) {
		return PaymentSuccessEvent.builder()
			.payment(paymentResult.payment())
			.reservation(paymentResult.reservation())
			.seat(paymentResult.seat())
			.user(paymentResult.user())
			.occurredAt(LocalDateTime.now())
			.build();
	}
}
