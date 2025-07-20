package kr.hhplus.be.server.payment.port.in;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.seat.CompletePaymentEvent;

public interface CompletePaymentUseCase {
	void completePayment(CompletePaymentEvent event) throws CustomException;
}
