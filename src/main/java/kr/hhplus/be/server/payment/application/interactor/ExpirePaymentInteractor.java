package kr.hhplus.be.server.payment.application.interactor;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.payment.application.service.PaymentApplicationService;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.ports.in.ExpirePaymentInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpirePaymentInteractor implements ExpirePaymentInput {

	private final PaymentApplicationService paymentApplicationService;

	@Override
	public Payment expirePayment(UUID reservationId) throws CustomException {
		return paymentApplicationService.expirePayment(reservationId);
	}
}
