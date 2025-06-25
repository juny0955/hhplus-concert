package kr.hhplus.be.server.usecase.queue;

import java.util.UUID;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.infrastructure.persistence.queue.QueueTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {

	private static final String LOCK_KEY = "queue:";

	private final QueueTokenManager queueTokenManager;
	private final DistributedLockManager distributedLockManager;

	public QueueToken issueQueueToken(UUID userId, UUID concertId) throws Exception {
		String lockKey = LOCK_KEY + concertId;

		return distributedLockManager.executeWithLock(
			lockKey,
			() -> queueTokenManager.processIssueQueueToken(userId, concertId)
		);
	}

	public QueueToken getQueueInfo(UUID concertId, String tokenId) throws CustomException {
		return queueTokenManager.getQueueInfo(concertId, tokenId);
	}
}
