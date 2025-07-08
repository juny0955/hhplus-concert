package kr.hhplus.be.server.payment.domain;

import java.time.LocalDateTime;

import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.user.domain.User;
import lombok.Builder;

@Builder
public record UpdateRankEvent(
	Payment payment,
	Reservation reservation,
	Seat seat,
	User user,
	LocalDateTime occurredAt
) {

	public static UpdateRankEvent from(PaymentTransactionResult paymentTransactionResult) {
		return UpdateRankEvent.builder()
			.payment(paymentTransactionResult.payment())
			.reservation(paymentTransactionResult.reservation())
			.seat(paymentTransactionResult.seat())
			.user(paymentTransactionResult.user())
			.occurredAt(LocalDateTime.now())
			.build();
	}
}
