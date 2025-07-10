package kr.hhplus.be.server.reservation.port.in.queue;

import java.util.UUID;

import kr.hhplus.be.server.reservation.domain.queue.QueueToken;

public interface IssueQueueTokenUseCase {
	QueueToken issueQueueToken(UUID userId, UUID concertId) throws Exception;
}
