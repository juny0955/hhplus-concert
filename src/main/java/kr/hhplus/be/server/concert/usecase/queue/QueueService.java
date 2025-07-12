package kr.hhplus.be.server.concert.usecase.queue;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.concert.domain.queue.QueueToken;
import kr.hhplus.be.server.concert.domain.queue.QueueTokenUtil;
import kr.hhplus.be.server.concert.port.in.queue.GetActiveQueueTokenUseCase;
import kr.hhplus.be.server.concert.port.in.queue.GetQueueTokenUseCase;
import kr.hhplus.be.server.concert.port.in.queue.IssueQueueTokenUseCase;
import kr.hhplus.be.server.concert.port.in.queue.PromoteQueueTokenUseCase;
import kr.hhplus.be.server.concert.port.out.queue.QueueTokenRepository;
import kr.hhplus.be.server.reservation.port.out.ConcertQueryPort;
import kr.hhplus.be.server.reservation.port.out.UserQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueService implements
	GetActiveQueueTokenUseCase,
	GetQueueTokenUseCase,
	IssueQueueTokenUseCase,
	PromoteQueueTokenUseCase {

	private static final int MAX_ACTIVE_TOKEN_SIZE = 50;
	private static final long QUEUE_EXPIRES_TIME = 30L;

	private final QueueTokenRepository queueTokenRepository;
	private final ConcertQueryPort concertQueryPort;
	private final UserQueryPort userQueryPort;

	@Override
	public QueueToken getActiveQueueToken(String tokenId) throws CustomException {
		QueueToken queueToken = queueTokenRepository.findQueueTokenByTokenId(tokenId);
		QueueTokenUtil.validateActiveQueueToken(queueToken);
		return queueToken;
	}

	@Override
	public QueueToken getQueueTokenInfo(UUID concertId, String tokenId) throws CustomException {
		concertQueryPort.existsConcert(concertId);
		QueueToken queueToken = queueTokenRepository.findQueueTokenByTokenId(tokenId);
		if (queueToken == null || queueToken.isExpired())
			throw new CustomException(ErrorCode.INVALID_QUEUE_TOKEN);

		if (queueToken.isActive())
			return queueToken;

		Integer waitingPosition = queueTokenRepository.findWaitingPosition(queueToken);

		return queueToken.withWaitingPosition(waitingPosition);
	}

	@Override
	@Transactional
	public QueueToken issueQueueToken(UUID userId, UUID concertId) throws Exception {
		concertQueryPort.existsConcert(concertId);
		userQueryPort.existsUser(userId);

		String findTokenId = queueTokenRepository.findTokenIdByUserIdAndConcertId(userId, concertId);
		if (findTokenId != null)
			return queueTokenRepository.findQueueTokenByTokenId(findTokenId);

		Integer activeTokens = queueTokenRepository.countActiveTokens(concertId);
		QueueToken queueToken = createQueueToken(activeTokens, userId, concertId);

		log.debug("대기열 토큰 발급: USER_ID - {}, CONCERT_ID - {}, 상태 - {}", userId, concertId, queueToken.status());
		queueTokenRepository.save(queueToken);
		return queueToken;
	}

	@Override
	public void promoteQueueToken(List<Concert> concerts) {
		for (Concert concert : concerts) {
			queueTokenRepository.promoteQueueToken(concert);
		}
	}

	private QueueToken createQueueToken(Integer activeTokens, UUID userId, UUID concertId) {
		UUID tokenId = UUID.randomUUID();

		if (activeTokens < MAX_ACTIVE_TOKEN_SIZE)
			return QueueToken.activeTokenOf(tokenId, userId, concertId, QUEUE_EXPIRES_TIME);

		Integer waitingTokens = queueTokenRepository.countWaitingTokens(concertId);
		return QueueToken.waitingTokenOf(tokenId, userId, concertId, waitingTokens);
	}

}
