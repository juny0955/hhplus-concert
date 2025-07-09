package kr.hhplus.be.server.domain.payment.dto;


import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.user.domain.User;
import lombok.Builder;

@Builder
public record PaymentResult(
	Payment payment,
	Seat seat,
	Reservation reservation,
	User user
) {
}
