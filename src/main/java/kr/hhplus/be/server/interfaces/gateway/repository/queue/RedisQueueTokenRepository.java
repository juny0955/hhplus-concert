package kr.hhplus.be.server.interfaces.gateway.repository.queue;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.entity.queue.QueueToken;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisQueueTokenRepository {

	private final RedisTemplate<String, Object> redisTemplate;

	public void save(QueueToken queueToken) {
	}

	public String findTokenIdByUserIdAndConcertId(UUID userId, UUID concertId) {
		return null;
	}

	public QueueToken findQueueTokenByTokenId(String tokenId) {
		return null;
	}

	public Integer countWaitingTokens(UUID concertId) {
		return null;
	}

	public Integer countActiveTokens(UUID concertId) {
		return null;
	}
}
