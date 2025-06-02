package kr.hhplus.be.server.usecase.queue;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.entity.queue.QueueToken;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.JpaConcertRepository;
import kr.hhplus.be.server.interfaces.gateway.repository.queue.RedisQueueTokenRepository;
import kr.hhplus.be.server.interfaces.gateway.repository.user.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {

	private final RedisQueueTokenRepository redisQueueTokenRepository;
	private final JpaConcertRepository jpaConcertRepository;
	private final JpaUserRepository jpaUserRepository;

	@Transactional
	public QueueToken issueQueueToken(UUID userId, UUID concertId) {
		return null;
	}

}
