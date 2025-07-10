package kr.hhplus.be.server.domain.payment.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;

public interface QueueTokenQueryPort {
    QueueToken getActiveToken(String tokenId) throws CustomException;
    void expireQueueToken(String tokenId);
}
