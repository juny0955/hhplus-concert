package kr.hhplus.be.server.interfaces.gateway.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, String> {
}
