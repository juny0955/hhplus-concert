package kr.hhplus.be.server.application.queue.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.concert.port.out.GetConcertPort;
import kr.hhplus.be.server.application.queue.port.in.GetQueueTokenInput;
import kr.hhplus.be.server.application.queue.service.QueueService;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GetQueueTokenInteractor implements GetQueueTokenInput {

	private final QueueService queueService;
	private final GetConcertPort getConcertPort;

	@Override
	public QueueToken getQueueTokenInfo(UUID concertId, String queueToken) throws CustomException {
		getConcertPort.existsConcert(concertId);
		return queueService.getQueueInfo(queueToken);
	}
}
