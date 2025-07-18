package kr.hhplus.be.server.concert.adapter.out.event.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.domain.seat.CompletePaymentEvent;
import kr.hhplus.be.server.concert.domain.seat.PaidReservationFailEvent;
import kr.hhplus.be.server.concert.port.out.ConcertEventPublishPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConcertKafkaEventPublisher implements ConcertEventPublishPort {

	private static final String PAID_SEAT_TOPIC = "paid-seat";
	private static final String PAID_RESERVATION_FAIL_TOPIC = "paid-reservation-fail";

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishCompletePaymentEvent(CompletePaymentEvent event) {
		log.info("결제 완료 이벤트 발행");
		kafkaTemplate.send(PAID_SEAT_TOPIC, event.seatId().toString(), event);
	}

	@Override
	public void publishPaidReservationFailEvent(PaidReservationFailEvent event) {
		log.info("좌석 결제 실패 이벤트 발행");
		kafkaTemplate.send(PAID_RESERVATION_FAIL_TOPIC, event.seatId().toString(), event);
	}
}
