package kr.hhplus.be.server.application.payment.adapters.out.queue;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.payment.port.out.ExpireQueueTokenPort;
import kr.hhplus.be.server.application.payment.port.out.GetActiveTokenPort;
import kr.hhplus.be.server.application.queue.port.in.ExpireQueueTokenUseCase;
import kr.hhplus.be.server.application.queue.port.in.GetActiveQueueTokenUseCase;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueTokenAdapter implements GetActiveTokenPort, ExpireQueueTokenPort {

	private final GetActiveQueueTokenUseCase getActiveQueueTokenUseCase;
	private final ExpireQueueTokenUseCase expireQueueTokenUseCase;

	@Override
	public QueueToken getActiveToken(String tokenId) throws CustomException {
		return getActiveQueueTokenUseCase.getActiveQueueToken(tokenId);
	}

	@Override
	public void expireQueueToken(String tokenId) {
		expireQueueTokenUseCase.expireQueueToken(tokenId);
	}
}
