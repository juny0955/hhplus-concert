package kr.hhplus.be.server.queue.port.in;

import java.util.UUID;

import kr.hhplus.be.server.queue.domain.QueueToken;

public interface IssueQueueTokenUseCase {
	QueueToken issueQueueToken(UUID userId, UUID concertId) throws Exception;
}
