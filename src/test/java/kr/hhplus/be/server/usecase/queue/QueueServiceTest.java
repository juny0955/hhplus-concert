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

import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.queue.QueueStatus;
import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.domain.queue.QueueTokenRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

	@InjectMocks
	private QueueService queueService;

	@Mock
	private QueueTokenRepository queueTokenRepository;

	@Mock
	private ConcertRepository concertRepository;

	@Mock
	private UserRepository userRepository;

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
		Integer waitingCount = 10;

		when(userRepository.existsById(userId)).thenReturn(true);
		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(queueTokenRepository.findTokenIdByUserIdAndConcertId(userId, concertId)).thenReturn(null);
		when(queueTokenRepository.countActiveTokens(concertId)).thenReturn(50);
		when(queueTokenRepository.countWaitingTokens(concertId)).thenReturn(waitingCount);

		QueueToken queueToken = queueService.issueQueueToken(userId, concertId);

		verify(userRepository, times(1)).existsById(userId);
		verify(concertRepository, times(1)).existsById(concertId);
		verify(queueTokenRepository, times(1)).findTokenIdByUserIdAndConcertId(userId, concertId);
		verify(queueTokenRepository, times(1)).countActiveTokens(concertId);
		verify(queueTokenRepository, times(1)).countWaitingTokens(concertId);
		verify(queueTokenRepository, times(1)).save(any(QueueToken.class));

		verify(queueTokenRepository, never()).findQueueTokenByTokenId(tokenId.toString());

		assertThat(queueToken.status()).isEqualTo(QueueStatus.WAITING);
		assertThat(queueToken.position()).isEqualTo(waitingCount + 1);
		assertThat(queueToken.issuedAt()).isNotNull();
		assertThat(queueToken.expiresAt()).isNull();
		assertThat(queueToken.enteredAt()).isNull();
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_성공(활성상태)") // 최대 활성 토큰 수 50개
	void issueQueueToken_Success_Active() throws CustomException {
		when(userRepository.existsById(userId)).thenReturn(true);
		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(queueTokenRepository.findTokenIdByUserIdAndConcertId(userId, concertId)).thenReturn(null);
		when(queueTokenRepository.countActiveTokens(concertId)).thenReturn(30);

		QueueToken queueToken = queueService.issueQueueToken(userId, concertId);

		verify(userRepository, times(1)).existsById(userId);
		verify(concertRepository, times(1)).existsById(concertId);
		verify(queueTokenRepository, times(1)).findTokenIdByUserIdAndConcertId(userId, concertId);
		verify(queueTokenRepository, times(1)).countActiveTokens(concertId);
		verify(queueTokenRepository, times(1)).save(any(QueueToken.class));

		verify(queueTokenRepository, never()).findQueueTokenByTokenId(tokenId.toString());

		assertThat(queueToken.status()).isEqualTo(QueueStatus.ACTIVE);
		assertThat(queueToken.issuedAt()).isNotNull();
		assertThat(queueToken.expiresAt()).isNotNull();
		assertThat(queueToken.enteredAt()).isNotNull();
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_성공(기존_토큰_존재)")
	void issueQueueToken_Success_existsToken() throws CustomException {
		when(userRepository.existsById(userId)).thenReturn(true);
		when(concertRepository.existsById(concertId)).thenReturn(true);
		when(queueTokenRepository.findTokenIdByUserIdAndConcertId(userId, concertId)).thenReturn(tokenId.toString());
		when(queueTokenRepository.findQueueTokenByTokenId(tokenId.toString())).thenReturn(existingToken);

		QueueToken queueToken = queueService.issueQueueToken(userId, concertId);

		verify(userRepository, times(1)).existsById(userId);
		verify(concertRepository, times(1)).existsById(concertId);
		verify(queueTokenRepository, times(1)).findTokenIdByUserIdAndConcertId(userId, concertId);
		verify(queueTokenRepository, times(1)).findQueueTokenByTokenId(tokenId.toString());

		verify(queueTokenRepository, never()).countActiveTokens(concertId);
		verify(queueTokenRepository, never()).save(any(QueueToken.class));

		assertThat(queueToken.tokenId()).isEqualTo(tokenId);
		assertThat(queueToken.status()).isEqualTo(QueueStatus.ACTIVE);
		assertThat(queueToken.issuedAt()).isNotNull();
		assertThat(queueToken.expiresAt()).isNotNull();
		assertThat(queueToken.enteredAt()).isNotNull();
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_실패_유저못찾음")
	void issueQueueToken_Failure_UserNotFound() {
		when(userRepository.existsById(userId)).thenReturn(false);

		CustomException customException = assertThrows(CustomException.class,
			() -> queueService.issueQueueToken(userId, concertId));

		verify(userRepository, times(1)).existsById(userId);
		verify(concertRepository, never()).existsById(concertId);
		verify(queueTokenRepository, never()).findTokenIdByUserIdAndConcertId(userId, concertId);
		verify(queueTokenRepository, never()).findQueueTokenByTokenId(tokenId.toString());
		verify(queueTokenRepository, never()).countActiveTokens(concertId);
		verify(queueTokenRepository, never()).save(any(QueueToken.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_실패_콘서트못찾음")
	void issueQueueToken_Failure_ConcertNotFound() {
		when(userRepository.existsById(userId)).thenReturn(true);
		when(concertRepository.existsById(concertId)).thenReturn(false);

		CustomException customException = assertThrows(CustomException.class,
			() -> queueService.issueQueueToken(userId, concertId));

		verify(userRepository, times(1)).existsById(userId);
		verify(concertRepository, times(1)).existsById(concertId);
		verify(queueTokenRepository, never()).findTokenIdByUserIdAndConcertId(userId, concertId);
		verify(queueTokenRepository, never()).findQueueTokenByTokenId(tokenId.toString());
		verify(queueTokenRepository, never()).countActiveTokens(concertId);
		verify(queueTokenRepository, never()).save(any(QueueToken.class));

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
	}
}