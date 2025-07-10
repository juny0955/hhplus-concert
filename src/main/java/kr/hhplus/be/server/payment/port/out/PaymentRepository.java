package kr.hhplus.be.server.payment.port.out;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.payment.domain.Payment;

public interface PaymentRepository {
	Payment save(Payment payment);

	Optional<Payment> findByReservationId(UUID reservationId);

	void deleteAll();
}
