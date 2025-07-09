package kr.hhplus.be.server.domain.queue.port.in;

import java.util.UUID;

import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.common.exception.CustomException;


public interface GetQueueTokenInput {
	QueueToken getQueueTokenInfo(UUID concertId, String queueToken) throws CustomException;
}
