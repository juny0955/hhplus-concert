package kr.hhplus.be.server.entity.queue;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record QueueToken(
	UUID tokenId,
	UUID userId,
	UUID concertId,
	QueueStatus status,
	Integer position,
	LocalDateTime issuedAt,
	LocalDateTime expiresAt,
	LocalDateTime enteredAt
) {
}
