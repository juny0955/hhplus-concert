package kr.hhplus.be.server.domain.payment.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.port.in.ExpirePaymentInput;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpirePaymentInteractor implements ExpirePaymentInput {

	private final PaymentService paymentService;

	@Override
	public Payment expirePayment(UUID reservationId) throws CustomException {
		return paymentService.expirePayment(reservationId);
	}
}
