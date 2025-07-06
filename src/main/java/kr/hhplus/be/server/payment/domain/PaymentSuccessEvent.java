package kr.hhplus.be.server.payment.domain;

import java.time.LocalDateTime;

import kr.hhplus.be.server.infrastructure.event.Event;
import kr.hhplus.be.server.infrastructure.event.EventTopic;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.infrastructure.persistence.payment.PaymentTransactionResult;
import lombok.Builder;

@Builder
public record PaymentSuccessEvent (
	Payment payment,
	Reservation reservation,
	Seat seat,
	User user,
	LocalDateTime occurredAt
) implements Event {

	public static PaymentSuccessEvent from(PaymentTransactionResult paymentTransactionResult) {
		return PaymentSuccessEvent.builder()
			.payment(paymentTransactionResult.payment())
			.reservation(paymentTransactionResult.reservation())
			.seat(paymentTransactionResult.seat())
			.user(paymentTransactionResult.user())
			.occurredAt(LocalDateTime.now())
			.build();
	}

	@Override
	public EventTopic getTopic() {
		return EventTopic.PAYMENT_SUCCESS;
	}

	@Override
	public String getKey() {
		return payment.id().toString();
	}
}
