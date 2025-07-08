package kr.hhplus.be.server.application.reservation.adapters.out.queue;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.queue.port.in.GetActiveQueueTokenUseCase;
import kr.hhplus.be.server.application.reservation.port.out.GetActiveTokenPort;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.exception.CustomException;
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
