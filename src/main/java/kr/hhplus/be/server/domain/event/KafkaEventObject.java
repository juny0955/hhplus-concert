package kr.hhplus.be.server.domain.event;

public record KafkaEventObject<T extends Event>(
	String topic,
	T payload
) {
}
