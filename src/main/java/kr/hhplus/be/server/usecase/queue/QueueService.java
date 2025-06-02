package kr.hhplus.be.server.usecase.queue;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.entity.queue.QueueToken;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.JpaConcertRepository;
import kr.hhplus.be.server.interfaces.gateway.repository.queue.RedisQueueTokenRepository;
import kr.hhplus.be.server.interfaces.gateway.repository.user.JpaUserRepository;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {

	private static final int MAX_ACTIVE_TOKEN_SIZE = 50;
	private static final long QUEUE_EXPIRES_TIME = 60L;

	private final RedisQueueTokenRepository redisQueueTokenRepository;
	private final JpaConcertRepository jpaConcertRepository;
	private final JpaUserRepository jpaUserRepository;

	@Transactional
	public QueueToken issueQueueToken(UUID userId, UUID concertId) throws CustomException {
		validateUserId(userId);
		validateConcertId(concertId);

		String findTokenId = redisQueueTokenRepository.findTokenIdByUserIdAndConcertId(userId, concertId);
		if (findTokenId != null)
			return redisQueueTokenRepository.findQueueTokenByTokenId(findTokenId);

		Integer activeTokens = redisQueueTokenRepository.countActiveTokens(concertId);

		UUID tokenId = UUID.randomUUID();
		QueueToken queueToken;
		if (activeTokens >= MAX_ACTIVE_TOKEN_SIZE) {
			Integer waitingTokens = redisQueueTokenRepository.countWaitingTokens(concertId);

			queueToken = QueueToken.waitingTokenOf(tokenId, userId, concertId, waitingTokens);
		} else {
			queueToken = QueueToken.activeTokenOf(tokenId, userId, concertId, QUEUE_EXPIRES_TIME);
		}

		redisQueueTokenRepository.save(queueToken);
		return queueToken;
	}

	private void validateUserId(UUID userId) throws CustomException {
		if (!jpaUserRepository.existsById(userId.toString()))
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
	}

	private void validateConcertId(UUID concertId) throws CustomException {
		if (!jpaConcertRepository.existsById(concertId.toString()))
			throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);
	}

}
