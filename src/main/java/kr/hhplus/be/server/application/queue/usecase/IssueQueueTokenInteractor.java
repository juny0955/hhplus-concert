package kr.hhplus.be.server.application.queue.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concert.port.out.GetConcertPort;
import kr.hhplus.be.server.application.queue.port.in.IssueQueueTokenInput;
import kr.hhplus.be.server.application.queue.service.QueueService;
import kr.hhplus.be.server.application.user.port.out.GetUserPort;
import kr.hhplus.be.server.config.aop.DistributedLock;
import kr.hhplus.be.server.domain.queue.QueueToken;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IssueQueueTokenInteractor implements IssueQueueTokenInput {

	private final QueueService queueService;
	private final GetConcertPort getConcertPort;
	private final GetUserPort getUserPort;

	@Override
	@DistributedLock(key = "queue:issue:#concertId")
	public QueueToken issueQueueToken(UUID userId, UUID concertId) throws Exception {
		getConcertPort.existsConcert(concertId);
		getUserPort.existsUser(userId);

		return queueService.processIssueQueueToken(userId, concertId);
	}
}
