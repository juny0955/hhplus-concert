package kr.hhplus.be.server.application.queue.usecase;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.queue.port.in.GetActiveQueueTokenInput;
import kr.hhplus.be.server.application.queue.service.QueueService;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.queue.QueueTokenUtil;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetActiveQueueTokenInteractor implements GetActiveQueueTokenInput {

	private final QueueService queueService;

	@Override
	public QueueToken getActiveQueueToken(String queueTokenId) throws CustomException {
		QueueToken queueToken = queueService.getQueueToken(queueTokenId);
		QueueTokenUtil.validateActiveQueueToken(queueToken);
		return queueToken;
	}
}
