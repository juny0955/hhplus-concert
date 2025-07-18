package kr.hhplus.be.server.user.adapter.out.event.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.user.domain.PaidUserEvent;
import kr.hhplus.be.server.user.port.out.UserEventPublishPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserKafkaEventPublisher implements UserEventPublishPort {

	private static final String PAID_USER_TOPIC = "paid-user";

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishPaidUserEvent(PaidUserEvent event) {
		kafkaTemplate.send(PAID_USER_TOPIC, event.userId().toString(), event);
	}
}
