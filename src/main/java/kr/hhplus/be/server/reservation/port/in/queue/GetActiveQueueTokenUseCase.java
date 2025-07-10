package kr.hhplus.be.server.reservation.port.in.queue;

import kr.hhplus.be.server.reservation.domain.queue.QueueToken;
import kr.hhplus.be.server.common.exception.CustomException;


public interface GetActiveQueueTokenUseCase {
	QueueToken getActiveQueueToken(String queueTokenId) throws CustomException;
}
