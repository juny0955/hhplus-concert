package kr.hhplus.be.server.dataplatform.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.dataplatform.port.in.PaymentSuccessUseCase;
import kr.hhplus.be.server.payment.domain.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataPlatformEventListener {

	private final PaymentSuccessUseCase paymentSuccessUseCase;

	@KafkaListener(topics = "payment-success", groupId = "dataplatform")
	public void paymentSuccessListener(PaymentSuccessEvent event) {
		log.info("결제 성공 이벤트 수신");
		paymentSuccessUseCase.sendDataPlatform(event);
	}
}
