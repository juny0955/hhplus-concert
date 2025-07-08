package kr.hhplus.be.server.application.queue.port.in;

public interface ExpireQueueTokenUseCase {
    void expireQueueToken(String tokenId);
}
