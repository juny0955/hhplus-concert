package kr.hhplus.be.server.usecase.event;

import kr.hhplus.be.server.domain.event.Event;
import kr.hhplus.be.server.domain.event.KafkaEventObject;

public interface EventPublisher {
	<T extends Event> void publish(KafkaEventObject<T> kafkaEventObject);
}
