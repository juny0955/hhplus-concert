package kr.hhplus.be.server.domain.queue.adapter.out.persistence;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.queue.port.out.QueueTokenRepository;
import kr.hhplus.be.server.domain.concert.domain.Concert;
import kr.hhplus.be.server.domain.queue.domain.QueueStatus;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.domain.queue.domain.QueueTokenUtil;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisQueueTokenRepository implements QueueTokenRepository {

	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisTemplate<String, QueueToken> queueTokenRedisTemplate;

	@Override
	public void save(QueueToken queueToken) {
		String tokenInfoKey = QueueTokenUtil.formattingTokenInfoKey(queueToken.tokenId());
		String tokenIdKey = QueueTokenUtil.formattingTokenIdKey(queueToken.userId(), queueToken.concertId());

		queueTokenRedisTemplate.opsForValue().set(tokenInfoKey, queueToken);
		redisTemplate.opsForValue().set(tokenIdKey, queueToken.tokenId().toString());

		if (queueToken.status().equals(QueueStatus.ACTIVE))
			saveActiveToken(queueToken, tokenIdKey);
		else
			saveWaitingToken(queueToken, tokenInfoKey, tokenIdKey);
	}

	@Override
	public String findTokenIdByUserIdAndConcertId(UUID userId, UUID concertId) {
		String tokenIdKey = QueueTokenUtil.formattingTokenIdKey(userId, concertId);
		Object tokenId = redisTemplate.opsForValue().get(tokenIdKey);
		return tokenId != null ? tokenId.toString() : null;
	}

	@Override
	public QueueToken findQueueTokenByTokenId(String tokenId) {
		String tokenInfoKey = QueueTokenUtil.formattingTokenInfoKey(UUID.fromString(tokenId));
		return queueTokenRedisTemplate.opsForValue().get(tokenInfoKey);
	}

	@Override
	public Integer findWaitingPosition(QueueToken queueToken) {
		String waitingTokenKey = QueueTokenUtil.formattingWaitingTokenKey(queueToken.concertId());
		String tokenIdKey = QueueTokenUtil.formattingTokenIdKey(queueToken.userId(), queueToken.concertId());

		Long rank = redisTemplate.opsForZSet().rank(waitingTokenKey, tokenIdKey);

		return rank != null ? rank.intValue() + 1 : null;
	}

	@Override
	public Integer countWaitingTokens(UUID concertId) {
		Long count = redisTemplate.opsForZSet().count(QueueTokenUtil.formattingWaitingTokenKey(concertId), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		return count != null ? count.intValue() : 0;
	}

	@Override
	public Integer countActiveTokens(UUID concertId) {
		Long count = redisTemplate.opsForSet().size(QueueTokenUtil.formattingActiveTokenKey(concertId));
		return count != null ? count.intValue() : 0;
	}

	@Override
	public void expiresQueueToken(String tokenId) {
		QueueToken queueToken = findQueueTokenByTokenId(tokenId);
		if (queueToken == null) return;

		String tokenInfoKey = QueueTokenUtil.formattingTokenInfoKey(queueToken.tokenId());
		String tokenIdKey = QueueTokenUtil.formattingTokenIdKey(queueToken.userId(), queueToken.concertId());

		redisTemplate.delete(tokenInfoKey);
		redisTemplate.delete(tokenIdKey);

		if (queueToken.status().equals(QueueStatus.ACTIVE))
			redisTemplate.opsForSet().remove(QueueTokenUtil.formattingActiveTokenKey(queueToken.concertId()), tokenIdKey);
		else
			redisTemplate.opsForZSet().remove(QueueTokenUtil.formattingWaitingTokenKey(queueToken.concertId()), tokenIdKey);
	}

	@Override
	public void promoteQueueToken(Concert openConcert) {
		String activeTokenKey = QueueTokenUtil.formattingActiveTokenKey(openConcert.id());
		String waitingTokenKey = QueueTokenUtil.formattingWaitingTokenKey(openConcert.id());

		List<String> keys = List.of(activeTokenKey, waitingTokenKey);

		redisTemplate.execute(QueueTokenUtil.promoteWaitingTokenScript(), keys, 50);
	}

	/**
	 * 대기 토큰 저장
	 * ZSet (Sorted Set) 자료구조 사용
	 * @param queueToken 토큰 정보
	 * @param tokenInfoKey 토큰 정보 Key
	 * @param tokenIdKey 토큰 ID Key
	 */
	private void saveWaitingToken(QueueToken queueToken, String tokenInfoKey, String tokenIdKey) {
		String waitingTokenKey = QueueTokenUtil.formattingWaitingTokenKey(queueToken.concertId());
		Instant issuedInstant = queueToken.issuedAt()
			.atZone(ZoneOffset.UTC)
			.toInstant();
		double score = issuedInstant.getEpochSecond();
		redisTemplate.opsForZSet().add(waitingTokenKey, tokenIdKey, score);

		redisTemplate.expire(tokenInfoKey, Duration.ofHours(24));
		redisTemplate.expire(tokenIdKey, Duration.ofHours(24));
	}

	/**
	 * 활설 토큰 저장
	 * @param queueToken 토큰 정보
	 * @param tokenIdKey 토큰 ID Key
	 */
	private void saveActiveToken(QueueToken queueToken, String tokenIdKey) {
		String activeTokenKey = QueueTokenUtil.formattingActiveTokenKey(queueToken.concertId());
		redisTemplate.opsForSet().add(activeTokenKey, tokenIdKey);
	}
}
