package kr.hhplus.be.server.domain.concert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record Seat(
	UUID id,
	int seatNo,
	BigDecimal price,
	SeatClass seatClass,
	SeatStatus status,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
