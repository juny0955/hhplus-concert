package kr.hhplus.be.server.user.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.payment.domain.PaymentEvent;
import kr.hhplus.be.server.user.port.in.UsePointUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

	private final UsePointUseCase usePointUseCase;

	@KafkaListener(topics = "payment", groupId = "user-payment")
	public void paymentEventListener(PaymentEvent event) throws Exception {
		usePointUseCase.usePoint(event);
	}
}
