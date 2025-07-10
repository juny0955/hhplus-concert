package kr.hhplus.be.server.reservation.port.in.queue;

public interface ExpireQueueTokenUseCase {
    void expireQueueToken(String tokenId);
}
