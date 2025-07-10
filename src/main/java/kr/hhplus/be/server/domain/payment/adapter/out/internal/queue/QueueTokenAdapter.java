package kr.hhplus.be.server.domain.payment.adapter.out.internal.queue;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.payment.port.out.QueueTokenQueryPort;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.domain.queue.port.in.ExpireQueueTokenUseCase;
import kr.hhplus.be.server.domain.queue.port.in.GetActiveQueueTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueTokenAdapter implements QueueTokenQueryPort {

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
