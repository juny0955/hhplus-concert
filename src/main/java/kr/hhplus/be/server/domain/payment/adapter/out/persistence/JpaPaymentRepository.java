package kr.hhplus.be.server.domain.payment.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, String> {
	Optional<PaymentEntity> findByReservationId(String reservationId);
}
