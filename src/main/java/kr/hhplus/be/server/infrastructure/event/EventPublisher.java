package kr.hhplus.be.server.infrastructure.event;

public interface EventPublisher {
	<T extends Event> void publish(T event);
}
