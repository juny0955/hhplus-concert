package kr.hhplus.be.server.queue.application.interactor;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.queue.application.service.QueueApplicationService;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.domain.QueueTokenUtil;
import kr.hhplus.be.server.queue.ports.in.GetActiveQueueTokenInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetActiveQueueTokenInteractor implements GetActiveQueueTokenInput {

	private final QueueApplicationService queueApplicationService;

	@Override
	public QueueToken getActiveQueueToken(String queueTokenId) throws CustomException {
		QueueToken queueToken = queueApplicationService.getQueueToken(queueTokenId);
		QueueTokenUtil.validateActiveQueueToken(queueToken);
		return queueToken;
	}
}
