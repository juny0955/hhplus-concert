package kr.hhplus.be.server.queue.ports.in;

import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.queue.domain.QueueToken;

public interface GetActiveQueueTokenInput {
	QueueToken getActiveQueueToken(String queueTokenId) throws CustomException;
}
