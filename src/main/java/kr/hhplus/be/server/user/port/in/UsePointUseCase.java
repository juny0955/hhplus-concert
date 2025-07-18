package kr.hhplus.be.server.user.port.in;

import kr.hhplus.be.server.payment.domain.PaymentEvent;

public interface UsePointUseCase {
	void usePoint(PaymentEvent event) throws Exception;
}
