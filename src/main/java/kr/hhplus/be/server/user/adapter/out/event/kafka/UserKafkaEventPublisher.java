package kr.hhplus.be.server.user.adapter.out.event.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.user.domain.PaidUserEvent;
import kr.hhplus.be.server.user.domain.PaymentFailEvent;
import kr.hhplus.be.server.user.port.out.UserEventPublishPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserKafkaEventPublisher implements UserEventPublishPort {

	private static final String PAID_USER_TOPIC = "paid-user";
	private static final String PAYMENT_FAIL_TOPIC = "payment-fail";

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishPaidUserEvent(PaidUserEvent event) {
		log.info("유저 결제 성공 이벤트 발행");
		kafkaTemplate.send(PAID_USER_TOPIC, event.userId().toString(), event);
	}

	@Override
	public void publishPaymentFailEvent(PaymentFailEvent event) {
		log.info("결제 실패 보상 이벤트 발행");
		kafkaTemplate.send(PAYMENT_FAIL_TOPIC, event.userId().toString(), event);
	}
}
