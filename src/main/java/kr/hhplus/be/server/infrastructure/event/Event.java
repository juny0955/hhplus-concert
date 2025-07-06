package kr.hhplus.be.server.infrastructure.event;

public interface Event {
	EventTopic getTopic();
	String getKey();
}
