package kr.hhplus.be.server.usecase.payment;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.payment.Payment;

public interface PaymentRepository {
	Payment save(Payment payment);

	Optional<Payment> findByReservationId(UUID reservationId);
}
