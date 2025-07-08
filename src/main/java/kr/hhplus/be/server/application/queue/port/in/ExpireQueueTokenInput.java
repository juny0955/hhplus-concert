package kr.hhplus.be.server.application.queue.port.in;

public interface ExpireQueueTokenInput {
    void expireQueueToken(String tokenId);
}
