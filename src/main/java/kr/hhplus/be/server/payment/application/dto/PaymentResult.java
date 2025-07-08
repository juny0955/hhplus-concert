package kr.hhplus.be.server.payment.application.dto;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.user.domain.User;
import lombok.Builder;

@Builder
public record PaymentResult(
	Payment payment,
	Seat seat,
	Reservation reservation,
	User user
) {
}
