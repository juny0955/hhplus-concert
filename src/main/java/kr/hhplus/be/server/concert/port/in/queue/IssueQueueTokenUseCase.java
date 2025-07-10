package kr.hhplus.be.server.concert.port.in.queue;

import java.util.UUID;

import kr.hhplus.be.server.concert.domain.queue.QueueToken;

public interface IssueQueueTokenUseCase {
	QueueToken issueQueueToken(UUID userId, UUID concertId) throws Exception;
}
