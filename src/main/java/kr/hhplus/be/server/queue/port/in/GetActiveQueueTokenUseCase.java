package kr.hhplus.be.server.queue.port.in;

import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.common.exception.CustomException;


public interface GetActiveQueueTokenUseCase {
	QueueToken getActiveQueueToken(String queueTokenId) throws CustomException;
}
