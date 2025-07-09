package kr.hhplus.be.server.domain.payment.port.out;

public interface ExpireQueueTokenPort {
	void expireQueueToken(String tokenId);
}
