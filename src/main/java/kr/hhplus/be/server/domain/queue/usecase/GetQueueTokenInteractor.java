package kr.hhplus.be.server.domain.queue.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.port.out.concert.GetConcertPort;
import kr.hhplus.be.server.domain.queue.port.in.GetQueueTokenInput;
import kr.hhplus.be.server.domain.queue.service.QueueService;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.common.exception.CustomException;
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
