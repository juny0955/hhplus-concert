package kr.hhplus.be.server.queue.application.service;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.common.framework.exception.ErrorCode;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.queue.ports.out.QueueTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueApplicationService {

	private static final int MAX_ACTIVE_TOKEN_SIZE = 50;
	private static final long QUEUE_EXPIRES_TIME = 30L;

	private final QueueTokenRepository queueTokenRepository;

	@Transactional
	public QueueToken processIssueQueueToken(UUID userId, UUID concertId) {
		String findTokenId = queueTokenRepository.findTokenIdByUserIdAndConcertId(userId, concertId);
		if (findTokenId != null)
			return getQueueToken(findTokenId);

		Integer activeTokens = queueTokenRepository.countActiveTokens(concertId);
		QueueToken queueToken = createQueueToken(activeTokens, userId, concertId);

		log.debug("대기열 토큰 발급: USER_ID - {}, CONCERT_ID - {}, 상태 - {}", userId, concertId, queueToken.status());
		queueTokenRepository.save(queueToken);
		return queueToken;
	}

	public QueueToken getQueueInfo(String tokenId) throws CustomException {
		QueueToken queueToken = getQueueToken(tokenId);
		if (queueToken == null || queueToken.isExpired())
			throw new CustomException(ErrorCode.INVALID_QUEUE_TOKEN);

		if (queueToken.isActive())
			return queueToken;

		Integer waitingPosition = queueTokenRepository.findWaitingPosition(queueToken);

		return queueToken.withWaitingPosition(waitingPosition);
	}

	public QueueToken getQueueToken(String tokenId) {
		return queueTokenRepository.findQueueTokenByTokenId(tokenId);
	}

	public void promoteQueueToken(List<Concert> concerts) {
		for (Concert concert : concerts) {
			queueTokenRepository.promoteQueueToken(concert);
		}
	}

	public void expireQueueToken(String tokenId) {
		queueTokenRepository.expiresQueueToken(tokenId);
	}

	private QueueToken createQueueToken(Integer activeTokens, UUID userId, UUID concertId) {
		UUID tokenId = UUID.randomUUID();

		if (activeTokens < MAX_ACTIVE_TOKEN_SIZE)
			return QueueToken.activeTokenOf(tokenId, userId, concertId, QUEUE_EXPIRES_TIME);

		Integer waitingTokens = queueTokenRepository.countWaitingTokens(concertId);
		return QueueToken.waitingTokenOf(tokenId, userId, concertId, waitingTokens);
	}

}
