package kr.hhplus.be.server.reservation.adapter.out.event.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.reservation.domain.PaidReservationEvent;
import kr.hhplus.be.server.reservation.port.out.ReservationEventPublishPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationKafkaEventPublisher implements ReservationEventPublishPort {

	private static final String PAID_RESERVATION_TOPIC = "paid-reservation";

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishPaidReservationEvent(PaidReservationEvent event) {
		kafkaTemplate.send(PAID_RESERVATION_TOPIC, event.reservationId().toString(), event);
	}
}
