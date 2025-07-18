package kr.hhplus.be.server.queue.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import kr.hhplus.be.server.queue.port.in.ExpireQueueTokenUseCase;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueEventListener {

	private final ExpireQueueTokenUseCase expireQueueTokenUseCase;

	@KafkaListener(topics = "payment-success", groupId = "expire-queue")
	public void paymentSuccessListener(PaymentSuccessEvent event) {
		expireQueueTokenUseCase.expireQueueToken(event);
	}
}
