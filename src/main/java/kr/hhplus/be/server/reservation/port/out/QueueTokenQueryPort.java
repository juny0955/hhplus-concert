package kr.hhplus.be.server.reservation.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.queue.QueueToken;

public interface QueueTokenQueryPort {
	QueueToken getActiveToken(String tokenId) throws CustomException;
}
