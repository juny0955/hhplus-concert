package kr.hhplus.be.server.domain.event.payment;

import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.domain.event.Event;
import kr.hhplus.be.server.domain.event.EventTopic;
import kr.hhplus.be.server.infrastructure.persistence.payment.PaymentTransactionResult;
import lombok.Builder;

@Builder
public record PaymentSuccessEvent (
	UUID paymentId,
	UUID reservationId,
	UUID seatId,
	UUID userId,
	LocalDateTime occurredAt
) implements Event {

	public static PaymentSuccessEvent from(PaymentTransactionResult paymentTransactionResult) {
		return PaymentSuccessEvent.builder()
			.paymentId(paymentTransactionResult.payment().id())
			.reservationId(paymentTransactionResult.reservation().id())
			.seatId(paymentTransactionResult.seat().id())
			.userId(paymentTransactionResult.user().id())
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
