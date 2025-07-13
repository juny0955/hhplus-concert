package kr.hhplus.be.server.queue.port.in;

import java.util.UUID;

import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.common.exception.CustomException;


public interface GetQueueTokenUseCase {
	QueueToken getQueueTokenInfo(UUID concertId, String queueToken) throws CustomException;
}
