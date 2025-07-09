package kr.hhplus.be.server.domain.reservation.port.out;

import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.common.exception.CustomException;

public interface GetActiveTokenPort {
	QueueToken getActiveToken(String tokenId) throws CustomException;
}
