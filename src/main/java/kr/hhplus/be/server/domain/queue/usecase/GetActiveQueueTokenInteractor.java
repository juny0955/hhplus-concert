package kr.hhplus.be.server.domain.queue.usecase;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.queue.port.in.GetActiveQueueTokenUseCase;
import kr.hhplus.be.server.domain.queue.service.QueueService;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.domain.queue.domain.QueueTokenUtil;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetActiveQueueTokenInteractor implements GetActiveQueueTokenUseCase {

	private final QueueService queueService;

	@Override
	public QueueToken getActiveQueueToken(String queueTokenId) throws CustomException {
		QueueToken queueToken = queueService.getQueueToken(queueTokenId);
		QueueTokenUtil.validateActiveQueueToken(queueToken);
		return queueToken;
	}
}
