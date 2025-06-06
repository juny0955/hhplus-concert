package kr.hhplus.be.server.domain.queue;

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

	public static QueueToken activeTokenOf(UUID tokenId, UUID userId, UUID concertId, long expiresTime) {
		LocalDateTime now = LocalDateTime.now();

		return QueueToken.builder()
			.tokenId(tokenId)
			.userId(userId)
			.concertId(concertId)
			.issuedAt(now)
			.enteredAt(now)
			.expiresAt(now.plusMinutes(expiresTime))
			.position(0)
			.status(QueueStatus.ACTIVE)
			.build();
	}

	public static QueueToken waitingTokenOf(UUID tokenId, UUID userId, UUID concertId, int waitingTokens) {
		return QueueToken.builder()
			.tokenId(tokenId)
			.userId(userId)
			.concertId(concertId)
			.issuedAt(LocalDateTime.now())
			.position(waitingTokens + 1)
			.status(QueueStatus.WAITING)
			.build();
	}

	public boolean isActive() {
		return status.equals(QueueStatus.ACTIVE);
	}
}
