package kr.hhplus.be.server.entity.concert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Seat(
	UUID id,
	UUID concertDateId,
	int seatNo,
	BigDecimal price,
	SeatClass seatClass,
	SeatStatus status,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
