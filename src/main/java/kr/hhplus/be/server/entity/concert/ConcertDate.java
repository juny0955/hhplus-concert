package kr.hhplus.be.server.entity.concert;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConcertDate(
	UUID id,
	UUID concertId,
	LocalDateTime date,
	LocalDateTime deadline,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
