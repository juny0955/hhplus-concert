package kr.hhplus.be.server.queue.ports.in;

import java.util.UUID;

import kr.hhplus.be.server.queue.domain.QueueToken;

public interface IssueQueueTokenInput {
	QueueToken issueQueueToken(UUID userId, UUID concertId) throws Exception;
}
