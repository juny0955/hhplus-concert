package kr.hhplus.be.server.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
	public static Payment of(UUID userId, UUID reservationId, BigDecimal amount, LocalDateTime now) {
		return Payment.builder()
			.userId(userId)
			.reservationId(reservationId)
			.amount(amount)
			.status(PaymentStatus.PENDING)
			.createdAt(now)
			.updatedAt(now)
			.build();
	}
}
