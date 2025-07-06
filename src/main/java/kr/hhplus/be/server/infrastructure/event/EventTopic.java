package kr.hhplus.be.server.infrastructure.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventTopic {

	RESERVATION_CREATED("reservation.created"),
	RESERVATION_EXPIRED("reservation.expired"),

	PAYMENT_SUCCESS("doPay.success"),
	;

	private final String topicName;
}
