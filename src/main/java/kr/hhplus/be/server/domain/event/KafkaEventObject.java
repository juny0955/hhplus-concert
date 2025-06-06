package kr.hhplus.be.server.domain.event;

import kr.hhplus.be.server.domain.event.reservation.ReservationCreatedEvent;

public record KafkaEventObject<T extends Event>(
	EventTopic topic,
	String key,
	T payload
) {
	public static KafkaEventObject<ReservationCreatedEvent> from(ReservationCreatedEvent reservationCreatedEvent) {
		return new KafkaEventObject<>(reservationCreatedEvent.getTopic(), reservationCreatedEvent.getKey(), reservationCreatedEvent);
	}
}
