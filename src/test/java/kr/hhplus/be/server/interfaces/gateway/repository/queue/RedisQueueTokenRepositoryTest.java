package kr.hhplus.be.server.interfaces.gateway.repository.queue;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
class RedisQueueTokenRepositoryTest {

	@InjectMocks
	private RedisQueueTokenRepository redisQueueTokenRepository;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;



}