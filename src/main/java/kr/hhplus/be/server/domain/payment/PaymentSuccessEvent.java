package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;

import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
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
