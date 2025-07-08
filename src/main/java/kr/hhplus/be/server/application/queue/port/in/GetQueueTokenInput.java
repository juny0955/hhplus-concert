package kr.hhplus.be.server.application.queue.port.in;

import java.util.UUID;

import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.exception.CustomException;


public interface GetQueueTokenInput {
	QueueToken getQueueTokenInfo(UUID concertId, String queueToken) throws CustomException;
}
