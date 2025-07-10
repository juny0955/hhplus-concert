package kr.hhplus.be.server.reservation.port.in.queue;

import java.util.UUID;

import kr.hhplus.be.server.reservation.domain.queue.QueueToken;
import kr.hhplus.be.server.common.exception.CustomException;


public interface GetQueueTokenUseCase {
	QueueToken getQueueTokenInfo(UUID concertId, String queueToken) throws CustomException;
}
