package kr.hhplus.be.server.payment.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.reservation.domain.queue.QueueToken;

public interface QueueTokenQueryPort {
    QueueToken getActiveToken(String tokenId) throws CustomException;
    void expireQueueToken(String tokenId);
}
