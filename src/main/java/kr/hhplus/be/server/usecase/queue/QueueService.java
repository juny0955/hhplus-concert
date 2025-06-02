package kr.hhplus.be.server.usecase.queue;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.entity.queue.QueueStatus;
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

	private final RedisQueueTokenRepository redisQueueTokenRepository;
	private final JpaConcertRepository jpaConcertRepository;
	private final JpaUserRepository jpaUserRepository;

	@Transactional
	public QueueToken issueQueueToken(UUID userId, UUID concertId) throws CustomException {
		if (!jpaUserRepository.existsById(userId.toString()))
			throw new CustomException(ErrorCode.USER_NOT_FOUND);

		if (!jpaConcertRepository.existsById(concertId.toString()))
			throw new CustomException(ErrorCode.CONCERT_NOT_FOUND);

		String findTokenId = redisQueueTokenRepository.findTokenIdByUserIdAndConcertId(userId, concertId);
		if (findTokenId != null)
			return redisQueueTokenRepository.findQueueTokenByTokenId(findTokenId);

		UUID tokenId = UUID.randomUUID();
		Integer activeTokens = redisQueueTokenRepository.countActiveTokens(concertId);
		QueueToken queueToken;
		if (activeTokens >= 50) {
			Integer waitingTokens = redisQueueTokenRepository.countWaitingTokens(concertId);
			queueToken = QueueToken.builder()
				.tokenId(tokenId)
				.userId(userId)
				.concertId(concertId)
				.issuedAt(LocalDateTime.now())
				.position(waitingTokens + 1)
				.status(QueueStatus.WAITING)
				.build();
		} else {
			queueToken = QueueToken.builder()
				.tokenId(tokenId)
				.userId(userId)
				.concertId(concertId)
				.issuedAt(LocalDateTime.now())
				.enteredAt(LocalDateTime.now())
				.expiresAt(LocalDateTime.now().plusMinutes(60))
				.position(0)
				.status(QueueStatus.ACTIVE)
				.build();
		}

		redisQueueTokenRepository.save(queueToken);
		return queueToken;
	}

}
