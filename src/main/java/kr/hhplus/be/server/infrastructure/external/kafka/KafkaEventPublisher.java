package kr.hhplus.be.server.infrastructure.external.kafka;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.event.Event;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaEventPublisher implements EventPublisher {

	@Override
	public <T extends Event> void publish(T event) {
		log.info("{} 이벤트 발생", event.getTopic());
	}
}
