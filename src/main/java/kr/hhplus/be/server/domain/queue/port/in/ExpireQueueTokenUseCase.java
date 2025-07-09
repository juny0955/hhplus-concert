package kr.hhplus.be.server.domain.queue.port.in;

public interface ExpireQueueTokenUseCase {
    void expireQueueToken(String tokenId);
}
