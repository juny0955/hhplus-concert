package kr.hhplus.be.server.usecase.queue;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.entity.queue.QueueStatus;
import kr.hhplus.be.server.entity.queue.QueueToken;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.JpaConcertRepository;
import kr.hhplus.be.server.interfaces.gateway.repository.queue.RedisQueueTokenRepository;
import kr.hhplus.be.server.interfaces.gateway.repository.user.JpaUserRepository;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

	@InjectMocks
	private QueueService queueService;

	@Mock
	private RedisQueueTokenRepository redisQueueTokenRepository;

	@Mock
	private JpaConcertRepository jpaConcertRepository;

	@Mock
	private JpaUserRepository jpaUserRepository;

	private UUID userId;
	private UUID concertId;
	private UUID tokenId;
	private QueueToken existingToken;

	@BeforeEach
	void beforeEach() {
		userId = UUID.randomUUID();
		concertId = UUID.randomUUID();
		tokenId = UUID.randomUUID();

		existingToken = QueueToken.builder()
			.tokenId(tokenId)
			.userId(userId)
			.concertId(concertId)
			.position(0)
			.issuedAt(LocalDateTime.now().minusMinutes(10))
			.expiresAt(LocalDateTime.now().plusMinutes(25))
			.enteredAt(LocalDateTime.now().minusMinutes(5))
			.status(QueueStatus.ACTIVE)
			.build();
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_성공(대기상태)") // 최대 활성 토큰 수 50개
	void issueQueueToken_Success_Waiting() throws CustomException {
		when(jpaUserRepository.existsById(userId.toString())).thenReturn(true);
		when(jpaConcertRepository.existsById(concertId.toString())).thenReturn(true);
		when(redisQueueTokenRepository.findTokenIdByUserIdAndConcertId(userId, concertId)).thenReturn(null);
		when(redisQueueTokenRepository.countActiveTokens(concertId)).thenReturn(50);

		QueueToken queueToken = queueService.issueQueueToken(userId, concertId);

		verify(jpaUserRepository, times(1)).existsById(userId.toString());
		verify(jpaConcertRepository, times(1)).existsById(concertId.toString());
		verify(redisQueueTokenRepository, times(1)).findTokenIdByUserIdAndConcertId(userId, concertId);
		verify(redisQueueTokenRepository, times(1)).countActiveTokens(concertId);
		verify(redisQueueTokenRepository, times(1)).save(any(QueueToken.class));

		verify(redisQueueTokenRepository, never()).findQueueTokenByTokenId(tokenId.toString());

		assertThat(queueToken.status()).isEqualTo(QueueStatus.WAITING);
		assertThat(queueToken.issuedAt()).isNotNull();
		assertThat(queueToken.expiresAt()).isNull();
		assertThat(queueToken.enteredAt()).isNull();
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_성공(활성상태)") // 최대 활성 토큰 수 50개
	void issueQueueToken_Success_Active() throws CustomException {
		when(jpaUserRepository.existsById(userId.toString())).thenReturn(true);
		when(jpaConcertRepository.existsById(concertId.toString())).thenReturn(true);
		when(redisQueueTokenRepository.findTokenIdByUserIdAndConcertId(userId, concertId)).thenReturn(null);
		when(redisQueueTokenRepository.countActiveTokens(concertId)).thenReturn(30);

		QueueToken queueToken = queueService.issueQueueToken(userId, concertId);

		verify(jpaUserRepository, times(1)).existsById(userId.toString());
		verify(jpaConcertRepository, times(1)).existsById(concertId.toString());
		verify(redisQueueTokenRepository, times(1)).findTokenIdByUserIdAndConcertId(userId, concertId);
		verify(redisQueueTokenRepository, times(1)).countActiveTokens(concertId);
		verify(redisQueueTokenRepository, times(1)).save(any(QueueToken.class));

		verify(redisQueueTokenRepository, never()).findQueueTokenByTokenId(tokenId.toString());

		assertThat(queueToken.status()).isEqualTo(QueueStatus.ACTIVE);
		assertThat(queueToken.issuedAt()).isNotNull();
		assertThat(queueToken.expiresAt()).isNotNull();
		assertThat(queueToken.enteredAt()).isNotNull();
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_성공(기존_토큰_존재)")
	void issueQueueToken_Success_existsToken() throws CustomException {
		when(jpaUserRepository.existsById(userId.toString())).thenReturn(true);
		when(jpaConcertRepository.existsById(concertId.toString())).thenReturn(true);
		when(redisQueueTokenRepository.findTokenIdByUserIdAndConcertId(userId, concertId)).thenReturn(tokenId.toString());
		when(redisQueueTokenRepository.findQueueTokenByTokenId(tokenId.toString())).thenReturn(existingToken);

		QueueToken queueToken = queueService.issueQueueToken(userId, concertId);

		verify(jpaUserRepository, times(1)).existsById(userId.toString());
		verify(jpaConcertRepository, times(1)).existsById(concertId.toString());
		verify(redisQueueTokenRepository, times(1)).findTokenIdByUserIdAndConcertId(userId, concertId);
		verify(redisQueueTokenRepository, times(1)).findQueueTokenByTokenId(tokenId.toString());

		verify(redisQueueTokenRepository, never()).countActiveTokens(concertId);
		verify(redisQueueTokenRepository, never()).save(any(QueueToken.class));

		assertThat(queueToken.tokenId()).isEqualTo(tokenId);
		assertThat(queueToken.status()).isEqualTo(QueueStatus.ACTIVE);
		assertThat(queueToken.issuedAt()).isNotNull();
		assertThat(queueToken.expiresAt()).isNotNull();
		assertThat(queueToken.enteredAt()).isNotNull();
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_실패_유저못찾음")
	void issueQueueToken_Failure_UserNotFound() {
		when(jpaUserRepository.existsById(userId.toString())).thenReturn(false);

		CustomException customException = assertThrows(CustomException.class,
			() -> queueService.issueQueueToken(userId, concertId));

		verify(jpaUserRepository, times(1)).existsById(userId.toString());
		verify(jpaConcertRepository, never()).existsById(concertId.toString());
		verify(redisQueueTokenRepository, never()).findTokenIdByUserIdAndConcertId(userId, concertId);
		verify(redisQueueTokenRepository, never()).findQueueTokenByTokenId(tokenId.toString());
		verify(redisQueueTokenRepository, never()).countActiveTokens(concertId);
		verify(redisQueueTokenRepository, never()).save(any(QueueToken.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_실패_콘서트못찾음")
	void issueQueueToken_Failure_ConcertNotFound() {
		when(jpaUserRepository.existsById(userId.toString())).thenReturn(true);
		when(jpaConcertRepository.existsById(concertId.toString())).thenReturn(false);

		CustomException customException = assertThrows(CustomException.class,
			() -> queueService.issueQueueToken(userId, concertId));

		verify(jpaUserRepository, times(1)).existsById(userId.toString());
		verify(jpaConcertRepository, times(1)).existsById(concertId.toString());
		verify(redisQueueTokenRepository, never()).findTokenIdByUserIdAndConcertId(userId, concertId);
		verify(redisQueueTokenRepository, never()).findQueueTokenByTokenId(tokenId.toString());
		verify(redisQueueTokenRepository, never()).countActiveTokens(concertId);
		verify(redisQueueTokenRepository, never()).save(any(QueueToken.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
	}
}