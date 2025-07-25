package kr.hhplus.be.server.queue.port.out;

import java.util.UUID;

import kr.hhplus.be.server.concert.domain.concert.Concert;
import kr.hhplus.be.server.queue.domain.QueueToken;

public interface QueueTokenRepository {
	/**
	 * 대기열 토큰 저장
	 * @param queueToken
	 */
	void save(QueueToken queueToken);

	/**
	 * 대기열 토큰 ID 찾기
	 * @param userId 사용자 ID
	 * @param concertId 콘서트 ID
	 * @return 대기열 토큰 ID
	 */
	String findTokenIdByUserIdAndConcertId(UUID userId, UUID concertId);

	/**
	 * 대기열 토큰 정보 찾기
	 * @param tokenId 대기열 토큰 ID (key)
	 * @return 대기열 토큰 정보
	 */
	QueueToken findQueueTokenByTokenId(String tokenId);

	/**
	 * 대기 토큰 포지션 찾기
	 * @param queueToken 대기열 토큰 정보
	 * @return 현재 포지션
	 */
	Integer findWaitingPosition(QueueToken queueToken);

	/**
	 * 대기 토큰 개수 찾기
	 * @param concertId 콘서트 ID
	 * @return 대기 토큰 개수
	 */
	Integer countWaitingTokens(UUID concertId);

	/**
	 * 활성 토큰 개수 찾기
	 * @param concertId 콘서트 ID
	 * @return 활성 토큰 개수
	 */
	Integer countActiveTokens(UUID concertId);

	/**
	 * 대기열 토큰 만료
	 * @param tokenId 대기열 토큰 ID
	 */
	void expiresQueueToken(String tokenId);

	void promoteQueueToken(Concert openConcert);
}
