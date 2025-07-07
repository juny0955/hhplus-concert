package kr.hhplus.be.server.payment.domain.service;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.reservation.domain.Reservation;

public record PaymentDomainResult(
	Reservation reservation,
	Payment payment
) {
}
