package kr.hhplus.be.server.concert.adapter.out.event.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.domain.seat.CompletePaymentEvent;
import kr.hhplus.be.server.concert.port.out.ConcertEventPublishPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertKafkaEventPublisher implements ConcertEventPublishPort {

	private static final String PAID_SEAT_TOPIC = "paid-seat";

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishCompletePaymentEvent(CompletePaymentEvent event) {
		kafkaTemplate.send(PAID_SEAT_TOPIC, event.seatId().toString(), event);
	}
}
