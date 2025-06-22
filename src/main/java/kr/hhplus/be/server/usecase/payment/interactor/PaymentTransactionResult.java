package kr.hhplus.be.server.usecase.payment.interactor;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;

public record PaymentTransactionResult(
	Payment payment,
	Reservation reservation,
	Seat seat,
	User user
) {
}
