package kr.hhplus.be.server.user.port.in;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.payment.domain.PaymentEvent;
import kr.hhplus.be.server.reservation.domain.PaidUserFailEvent;

public interface UsePointUseCase {
	/**
	 * 포인트 결제
	 * @param event 결제 이벤트
	 * @throws Exception
	 */
	void usePoint(PaymentEvent event) throws Exception;

	/**
	 * 포인트 결제 성공 이벤트 실패 - 보상 트랜잭션
	 * @param event 포인트 결제 실패 이벤트
	 * @throws CustomException
	 */
	void failUsePoint(PaidUserFailEvent event) throws CustomException;
}
