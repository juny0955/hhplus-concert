package kr.hhplus.be.server.domain.event.payment;

import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.domain.event.Event;
import kr.hhplus.be.server.domain.event.EventTopic;
import lombok.Builder;

@Builder
public record PaymentSuccessEvent (
	UUID paymentId,
	UUID reservationId,
	UUID seatId,
	UUID userId,
	LocalDateTime occurredAt
) implements Event {

	public static PaymentSuccessEvent of(UUID paymentId, UUID reservationId, UUID seatId, UUID userId) {
		return PaymentSuccessEvent.builder()
			.paymentId(paymentId)
			.reservationId(reservationId)
			.seatId(seatId)
			.userId(userId)
			.occurredAt(LocalDateTime.now())
			.build();
	}

	@Override
	public EventTopic getTopic() {
		return EventTopic.PAYMENT_SUCCESS;
	}

	@Override
	public String getKey() {
		return paymentId.toString();
	}
}
