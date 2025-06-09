package kr.hhplus.be.server.infrastructure.external.kafka;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.event.Event;
import kr.hhplus.be.server.usecase.event.EventPublisher;

@Component
public class KafkaEventPublisher implements EventPublisher {

	@Override
	public <T extends Event> void publish(T event) {

	}
}
