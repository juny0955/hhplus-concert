package kr.hhplus.be.server.infrastructure.persistence.payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, String> {
	Optional<PaymentEntity> findByReservationId(String reservationId);

	@Query("select p from PaymentEntity p where p.reservationId = :reservationId")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<PaymentEntity> findByReservationIdForUpdate(String reservationId);
}
