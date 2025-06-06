package kr.hhplus.be.server.usecase.queue;

import java.util.UUID;

import kr.hhplus.be.server.domain.queue.QueueToken;

public interface QueueTokenRepository {
	void save(QueueToken queueToken);
	String findTokenIdByUserIdAndConcertId(UUID userId, UUID concertId);
	QueueToken findQueueTokenByTokenId(String tokenId);
	Integer countWaitingTokens(UUID concertId);
	Integer countActiveTokens(UUID concertId);
}
