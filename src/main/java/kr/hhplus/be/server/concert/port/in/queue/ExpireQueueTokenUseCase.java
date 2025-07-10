package kr.hhplus.be.server.concert.port.in.queue;

public interface ExpireQueueTokenUseCase {
    void expireQueueToken(String tokenId);
}
