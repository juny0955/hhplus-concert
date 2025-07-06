package kr.hhplus.be.server.payment.domain;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
	Payment save(Payment payment);

	Optional<Payment> findByReservationId(UUID reservationId);

	void deleteAll();
}
