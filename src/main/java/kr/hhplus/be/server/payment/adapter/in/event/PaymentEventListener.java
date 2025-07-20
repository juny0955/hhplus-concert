package kr.hhplus.be.server.payment.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.CompletePaymentEvent;
import kr.hhplus.be.server.payment.port.in.CompletePaymentUseCase;
import kr.hhplus.be.server.user.domain.PaymentFailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

	private final CompletePaymentUseCase completePaymentUseCase;

	@KafkaListener(topics = "complete-payment", groupId = "complete-payment")
	public void completePaymentEventListener(CompletePaymentEvent event) throws CustomException {
		log.info("결제 완료 이벤트 수신");
		completePaymentUseCase.completePayment(event);
	}

	@KafkaListener(topics = "payment-fail", groupId = "payment-fail")
	public void paymentFailEventListener(PaymentFailEvent event) {
		log.info("결제 실패 이벤트 수신");
	}
}
