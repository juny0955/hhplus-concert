package kr.hhplus.be.server.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventTopic {

	RESERVATION_CREATED("reservation.created")
	;

	private String topicName;
}
