package kr.hhplus.be.server.application.payment.port.out;

public interface ExpireQueueTokenPort {
	void expireQueueToken(String tokenId);
}
