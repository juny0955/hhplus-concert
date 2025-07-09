package kr.hhplus.be.server.domain.reservation.adapter.out.queue;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.queue.port.in.GetActiveQueueTokenUseCase;
import kr.hhplus.be.server.domain.reservation.port.out.GetActiveTokenPort;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueTokenAdapter implements GetActiveTokenPort {

	private final GetActiveQueueTokenUseCase getActiveQueueTokenUseCase;

	@Override
	public QueueToken getActiveToken(String tokenId) throws CustomException {
		return getActiveQueueTokenUseCase.getActiveQueueToken(tokenId);
	}

}
