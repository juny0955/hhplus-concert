package kr.hhplus.be.server.queue.application.interactor;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.queue.application.service.QueueApplicationService;
import kr.hhplus.be.server.queue.ports.in.PromoteQueueTokenInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PromoteQueueTokenInteractor implements PromoteQueueTokenInput {

	private final QueueApplicationService queueApplicationService;

	@Override
	public void promoteQueueToken(List<Concert> concerts) {
		queueApplicationService.promoteQueueToken(concerts);
	}
}
