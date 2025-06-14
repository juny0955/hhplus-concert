package kr.hhplus.be.server.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventTopic {

	RESERVATION_CREATED("reservation.created"),
	PAYMENT_SUCCESS("payment.success")
	;

	private final String topicName;
}
