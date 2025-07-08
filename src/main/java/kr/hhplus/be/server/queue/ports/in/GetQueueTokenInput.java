package kr.hhplus.be.server.queue.ports.in;

import java.util.UUID;

import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.queue.domain.QueueToken;

public interface GetQueueTokenInput {
	QueueToken getQueueTokenInfo(UUID concertId, String queueToken) throws CustomException;
}
