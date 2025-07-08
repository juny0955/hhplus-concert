package kr.hhplus.be.server.queue.application.interactor;

import java.util.UUID;

import kr.hhplus.be.server.concert.ports.in.concert.ExistsConcertInput;
import kr.hhplus.be.server.user.ports.in.ExistsUserInput;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.infrastructure.persistence.lock.DistributedLockManager;

import kr.hhplus.be.server.queue.application.service.QueueApplicationService;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.in.IssueQueueTokenInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IssueQueueTokenInteractor implements IssueQueueTokenInput {

	private static final String LOCK_KEY = "queue:issue:";

	private final QueueApplicationService queueApplicationService;
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
			() -> queueApplicationService.processIssueQueueToken(userId, concertId)
		);
	}
}
