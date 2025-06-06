package kr.hhplus.be.server.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.interfaces.gateway.repository.payment.PaymentEntity;
import lombok.Builder;

@Builder
public record Payment(
	UUID id,
	UUID userId,
	UUID reservationId,
	BigDecimal amount,
	PaymentStatus status,
	String failureReason,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static Payment of(UUID userId, UUID reservationId, BigDecimal amount) {
		return Payment.builder()
			.userId(userId)
			.reservationId(reservationId)
			.amount(amount)
			.status(PaymentStatus.PENDING)
			.build();
	}

	public static Payment from(PaymentEntity paymentEntity) {
		return Payment.builder()
			.id(UUID.fromString(paymentEntity.getId()))
			.userId(UUID.fromString(paymentEntity.getUserId()))
			.reservationId(UUID.fromString(paymentEntity.getReservationId()))
			.amount(paymentEntity.getAmount())
			.status(paymentEntity.getStatus())
			.failureReason(paymentEntity.getFailureReason())
			.createdAt(paymentEntity.getCreatedAt())
			.updatedAt(paymentEntity.getUpdatedAt())
			.build();
	}
}
