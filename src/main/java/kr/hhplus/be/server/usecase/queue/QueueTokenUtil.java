package kr.hhplus.be.server.usecase.queue;

import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;

public final class QueueTokenUtil {

	public static void validateQueueToken(QueueToken queueToken) throws CustomException {
		if (queueToken == null || !queueToken.isActive())
			throw new CustomException(ErrorCode.INVALID_QUEUE_TOKEN);
	}
}
