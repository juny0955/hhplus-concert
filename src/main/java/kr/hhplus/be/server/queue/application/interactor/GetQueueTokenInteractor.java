package kr.hhplus.be.server.queue.application.interactor;

import java.util.UUID;

import kr.hhplus.be.server.concert.ports.in.concert.ExistsConcertInput;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.queue.application.service.QueueApplicationService;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.in.GetQueueTokenInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetQueueTokenInteractor implements GetQueueTokenInput {

	private final QueueApplicationService queueApplicationService;
	private final ExistsConcertInput existsConcertInput;

	@Override
	public QueueToken getQueueTokenInfo(UUID concertId, String queueToken) throws CustomException {
		existsConcertInput.existsConcert(concertId);
		return queueApplicationService.getQueueInfo(queueToken);
	}
}
