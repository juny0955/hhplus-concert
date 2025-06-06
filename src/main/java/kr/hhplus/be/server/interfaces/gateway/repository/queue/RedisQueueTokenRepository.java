package kr.hhplus.be.server.interfaces.gateway.repository.queue;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.usecase.queue.QueueTokenRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisQueueTokenRepository implements QueueTokenRepository {

	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public void save(QueueToken queueToken) {
	}

	@Override
	public String findTokenIdByUserIdAndConcertId(UUID userId, UUID concertId) {
		return null;
	}

	@Override
	public QueueToken findQueueTokenByTokenId(String tokenId) {
		return null;
	}

	@Override
	public Integer countWaitingTokens(UUID concertId) {
		return null;
	}

	@Override
	public Integer countActiveTokens(UUID concertId) {
		return null;
	}
}
