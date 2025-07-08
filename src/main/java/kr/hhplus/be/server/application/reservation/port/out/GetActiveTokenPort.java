package kr.hhplus.be.server.application.reservation.port.out;

import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.exception.CustomException;

public interface GetActiveTokenPort {
	QueueToken getActiveToken(String tokenId) throws CustomException;
}
