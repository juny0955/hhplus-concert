package kr.hhplus.be.server.payment.adapter.out.event.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import kr.hhplus.be.server.payment.port.out.EventPublishPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentKafkaEventPublisher implements EventPublishPort {

	private static final String PAYMENT_SUCCESS_TOPIC = "payment-success";

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishPaymentSuccessEvent(PaymentSuccessEvent event) {
		kafkaTemplate.send(PAYMENT_SUCCESS_TOPIC, event);
	}
}
