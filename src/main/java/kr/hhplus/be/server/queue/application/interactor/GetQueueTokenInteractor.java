package kr.hhplus.be.server.queue.application.interactor;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.queue.application.service.QueueApplicationService;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.in.GetQueueTokenInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetQueueTokenInteractor implements GetQueueTokenInput {

	private final QueueApplicationService queueApplicationService;

	@Override
	public QueueToken getQueueTokenInfo(UUID concertId, String queueToken) throws CustomException {
		return queueApplicationService.getQueueInfo(concertId, queueToken);
	}
}
