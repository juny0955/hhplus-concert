package kr.hhplus.be.server.domain.queue.usecase;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.queue.service.QueueService;
import kr.hhplus.be.server.domain.queue.port.in.PromoteQueueTokenInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PromoteQueueTokenInteractor implements PromoteQueueTokenInput {

	private final QueueService queueService;

	@Override
	public void promoteQueueToken(List<Concert> concerts) {
		queueService.promoteQueueToken(concerts);
	}
}
