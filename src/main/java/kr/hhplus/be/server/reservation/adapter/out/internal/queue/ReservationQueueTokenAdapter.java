package kr.hhplus.be.server.reservation.adapter.out.internal.queue;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.port.in.GetActiveQueueTokenUseCase;
import kr.hhplus.be.server.reservation.port.out.QueueTokenQueryPort;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationQueueTokenAdapter implements QueueTokenQueryPort {

	private final GetActiveQueueTokenUseCase getActiveQueueTokenUseCase;

	@Override
	public QueueToken getActiveToken(String tokenId) throws CustomException {
		return getActiveQueueTokenUseCase.getActiveQueueToken(tokenId);
	}
}
