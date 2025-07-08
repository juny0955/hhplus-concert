package kr.hhplus.be.server.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.ErrorCode;
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

	public Payment success() throws CustomException {
		if (!status.equals(PaymentStatus.PENDING))
			throw new CustomException(ErrorCode.PAYMENT_STATUS_NOT_PENDING);

		return Payment.builder()
			.id(id)
			.userId(userId)
			.reservationId(reservationId)
			.amount(amount)
			.status(PaymentStatus.SUCCESS)
			.createdAt(createdAt)
			.updatedAt(LocalDateTime.now())
			.build();
	}

	public Payment expired() throws CustomException {
		if (!status.equals(PaymentStatus.PENDING))
			throw new CustomException(ErrorCode.PAYMENT_STATUS_NOT_PENDING);

		return Payment.builder()
				.id(id)
				.userId(userId)
				.reservationId(reservationId)
				.amount(amount)
				.status(PaymentStatus.FAILED)
				.failureReason("임시 배정이 만료되었습니다.")
				.createdAt(createdAt)
				.updatedAt(LocalDateTime.now())
				.build();
	}

	public boolean isPaid() {
		return status.equals(PaymentStatus.SUCCESS);
	}

	public boolean checkAmount() {
		return amount().compareTo(BigDecimal.ZERO) > 0;
	}
}
