package kr.hhplus.be.server.payment.adapter.out.internal.queue;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.concert.domain.queue.QueueToken;
import kr.hhplus.be.server.concert.port.in.queue.GetActiveQueueTokenUseCase;
import kr.hhplus.be.server.payment.port.out.QueueTokenQueryPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueTokenAdapter implements QueueTokenQueryPort {

	private final GetActiveQueueTokenUseCase getActiveQueueTokenUseCase;

	@Override
	public QueueToken getActiveToken(String tokenId) throws CustomException {
		return getActiveQueueTokenUseCase.getActiveQueueToken(tokenId);
	}
}
