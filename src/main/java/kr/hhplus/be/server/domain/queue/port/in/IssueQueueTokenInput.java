package kr.hhplus.be.server.domain.queue.port.in;

import java.util.UUID;

import kr.hhplus.be.server.domain.queue.domain.QueueToken;

public interface IssueQueueTokenInput {
	QueueToken issueQueueToken(UUID userId, UUID concertId) throws Exception;
}
