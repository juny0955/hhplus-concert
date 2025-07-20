package kr.hhplus.be.server.reservation.adapter.out.event.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.reservation.domain.PaidReservationEvent;
import kr.hhplus.be.server.reservation.domain.PaidUserFailEvent;
import kr.hhplus.be.server.reservation.port.out.ReservationEventPublishPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationKafkaEventPublisher implements ReservationEventPublishPort {

	private static final String PAID_RESERVATION_TOPIC = "paid-reservation";
	private static final String PAID_USER_FAIL_TOPIC = "paid-user-fail";

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishPaidReservationEvent(PaidReservationEvent event) {
		log.info("예약 결제 성공 이벤트 발행");
		kafkaTemplate.send(PAID_RESERVATION_TOPIC, event.reservationId().toString(), event);
	}

	@Override
	public void publishPaidUserFailEvent(PaidUserFailEvent event) {
		log.info("예약 결제 실패 이벤트 발행");
		kafkaTemplate.send(PAID_USER_FAIL_TOPIC, event.reservationId().toString(), event);
	}
}
