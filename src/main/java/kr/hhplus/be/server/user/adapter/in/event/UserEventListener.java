package kr.hhplus.be.server.user.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.payment.domain.PaymentEvent;
import kr.hhplus.be.server.reservation.domain.PaidUserFailEvent;
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
		log.info("유저 결제 이벤트 수신");
		usePointUseCase.usePoint(event);
	}

	@KafkaListener(topics = "paid-user-fail", groupId = "paid-user-fail")
	public void paidUserFailEventListener(PaidUserFailEvent event) throws CustomException {
		log.info("예약 결제 실패 이벤트 수신");
		usePointUseCase.failUsePoint(event);
	}
}
