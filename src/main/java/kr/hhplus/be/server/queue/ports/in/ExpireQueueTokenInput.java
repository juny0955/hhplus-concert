package kr.hhplus.be.server.queue.ports.in;

public interface ExpireQueueTokenInput {
    void expireQueueToken(String tokenId);
}
