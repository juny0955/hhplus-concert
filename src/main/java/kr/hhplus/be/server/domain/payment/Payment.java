package kr.hhplus.be.server.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
}
