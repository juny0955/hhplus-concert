package kr.hhplus.be.server.domain.queue.port.in;

import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.common.exception.CustomException;


public interface GetActiveQueueTokenUseCase {
	QueueToken getActiveQueueToken(String queueTokenId) throws CustomException;
}
