package kr.hhplus.be.server.payment.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.CompletePaymentEvent;
import kr.hhplus.be.server.payment.port.in.CompletePaymentUseCase;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

	private final CompletePaymentUseCase completePaymentUseCase;

	@KafkaListener(topics = "complete-payment", groupId = "complete-payment")
	public void completePaymentEventListener(CompletePaymentEvent event) throws CustomException {
		completePaymentUseCase.completePayment(event);
	}
}
