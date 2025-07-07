package kr.hhplus.be.server.payment.domain.service;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.reservation.domain.Reservation;

@Component
public class PaymentDomainService {

	public PaymentDomainResult processPayment(Reservation reservation, Payment payment) throws CustomException {
		validatePayment(payment);

		Reservation paidReservation = reservation.doPay();
		Payment paidPayment 		= payment.success();

		return new PaymentDomainResult(paidReservation, paidPayment);
	}

	private void validatePayment(Payment payment) throws CustomException {
		if (!payment.checkAmount())
			throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);

		if (payment.isPaid())
			throw new CustomException(ErrorCode.ALREADY_PAID);
	}
}
