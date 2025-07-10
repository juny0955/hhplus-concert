package kr.hhplus.be.server.domain.reservation.adapter.out.internal.queue;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.domain.queue.port.in.GetActiveQueueTokenUseCase;
import kr.hhplus.be.server.domain.reservation.port.out.QueueTokenQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueTokenAdapter implements QueueTokenQueryPort {

	private final GetActiveQueueTokenUseCase getActiveQueueTokenUseCase;

	@Override
	public QueueToken getActiveToken(String tokenId) throws CustomException {
		return getActiveQueueTokenUseCase.getActiveQueueToken(tokenId);
	}

}
