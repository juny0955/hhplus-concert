package kr.hhplus.be.server.interfaces.gateway.repository.queue;

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
}
