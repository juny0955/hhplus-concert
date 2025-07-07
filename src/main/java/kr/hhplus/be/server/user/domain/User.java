package kr.hhplus.be.server.user.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.Builder;

@Builder
public record User (
	UUID id,
	BigDecimal amount,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public User chargePoint(BigDecimal point) {
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
}
