package kr.hhplus.be.server.application.queue.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.adapters.out.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.application.concert.port.in.ExistsConcertInput;
import kr.hhplus.be.server.application.queue.port.in.IssueQueueTokenInput;
import kr.hhplus.be.server.application.queue.service.QueueService;
import kr.hhplus.be.server.application.user.port.in.ExistsUserInput;
import kr.hhplus.be.server.domain.queue.QueueToken;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IssueQueueTokenInteractor implements IssueQueueTokenInput {

	private static final String LOCK_KEY = "queue:issue:";

	private final QueueService queueService;
	private final DistributedLockManager distributedLockManager;
	private final ExistsConcertInput existsConcertInput;
	private final ExistsUserInput existsUserInput;

	@Override
	public QueueToken issueQueueToken(UUID userId, UUID concertId) throws Exception {
		existsConcertInput.existsConcert(concertId);
		existsUserInput.existsUser(userId);

		String lockKey = LOCK_KEY + concertId;

		return distributedLockManager.executeWithLockHasReturn(
			lockKey,
			() -> queueService.processIssueQueueToken(userId, concertId)
		);
	}
}
