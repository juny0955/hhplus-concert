package kr.hhplus.be.server.application.queue.port.in;

import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.exception.CustomException;


public interface GetActiveQueueTokenUseCase {
	QueueToken getActiveQueueToken(String queueTokenId) throws CustomException;
}
