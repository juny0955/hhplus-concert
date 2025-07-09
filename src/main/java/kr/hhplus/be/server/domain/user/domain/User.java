package kr.hhplus.be.server.domain.user.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Builder;

@Builder
public record User (
	UUID id,
	BigDecimal amount,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	private static final BigDecimal MIN_CHARGE_POINT = BigDecimal.valueOf(1000);

	public User chargePoint(BigDecimal point) throws CustomException {
		validateChargeAmount(point);

		return User.builder()
			.id(id)
			.amount(amount.add(point))
			.updatedAt(updatedAt)
			.build();
	}

	public User usePoint(BigDecimal balance) throws CustomException {
		if (!checkEnoughAmount(balance))
			throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);

		return User.builder()
			.id(id)
			.amount(amount.subtract(balance))
			.updatedAt(updatedAt)
			.build();
	}

	public boolean checkEnoughAmount(BigDecimal balance) {
		return amount.compareTo(balance) >= 0;
	}

	private void validateChargeAmount(BigDecimal chargeAmount) throws CustomException {
		if (chargeAmount.compareTo(MIN_CHARGE_POINT) < 0)
			throw new CustomException(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT);
	}
}
