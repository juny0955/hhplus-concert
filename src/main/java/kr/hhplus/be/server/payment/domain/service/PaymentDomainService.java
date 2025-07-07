package kr.hhplus.be.server.payment.domain.service;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentDomainService {

	public PaymentDomainResult processPayment(Reservation reservation, Payment payment, Seat seat, User user) throws CustomException {
		validatePayment(payment);
		validateUserBalance(payment, user);

		User paidUser 				= user.doPay(payment.amount());
		Reservation paidReservation = reservation.doPay();
		Payment paidPayment 		= payment.success();
		Seat paidSeat 				= seat.payment();

		return new PaymentDomainResult(paidUser, paidReservation, paidPayment, paidSeat);
	}

	private void validateUserBalance(Payment payment, User user) throws CustomException {
		if (!user.checkEnoughAmount(payment.amount()))
			throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
	}

	private void validatePayment(Payment payment) throws CustomException {
		if (!payment.checkAmount())
			throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);

		if (payment.isPaid())
			throw new CustomException(ErrorCode.ALREADY_PAID);
	}
}
