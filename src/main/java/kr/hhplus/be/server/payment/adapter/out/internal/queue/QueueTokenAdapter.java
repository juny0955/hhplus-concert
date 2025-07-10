package kr.hhplus.be.server.payment.adapter.out.internal.queue;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.payment.port.out.QueueTokenQueryPort;
import kr.hhplus.be.server.reservation.domain.queue.QueueToken;
import kr.hhplus.be.server.reservation.port.in.queue.ExpireQueueTokenUseCase;
import kr.hhplus.be.server.reservation.port.in.queue.GetActiveQueueTokenUseCase;
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
