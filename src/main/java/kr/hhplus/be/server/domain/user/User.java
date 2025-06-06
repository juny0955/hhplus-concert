package kr.hhplus.be.server.domain.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record User (
	UUID id,
	BigDecimal amount,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
