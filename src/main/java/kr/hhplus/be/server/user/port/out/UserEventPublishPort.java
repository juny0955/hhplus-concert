package kr.hhplus.be.server.user.port.out;

import kr.hhplus.be.server.user.domain.PaidUserEvent;
import kr.hhplus.be.server.user.domain.PaymentFailEvent;

public interface UserEventPublishPort {
	/**
	 * 포인트 결제 성공 이벤트 발행
	 * @param event 포인트 결제 성공 이벤트
	 */
	void publishPaidUserEvent(PaidUserEvent event);

	/**
	 * 결제 실패 이벤트 발행
	 * @param event 결제 실패 이벤트
	 */
	void publishPaymentFailEvent(PaymentFailEvent event);
}
