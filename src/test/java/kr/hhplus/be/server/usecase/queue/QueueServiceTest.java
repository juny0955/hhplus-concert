package kr.hhplus.be.server.usecase.queue;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.common.aop.DistributedLockAspect;
import kr.hhplus.be.server.domain.queue.service.QueueService;
import kr.hhplus.be.server.domain.queue.domain.QueueStatus;
import kr.hhplus.be.server.domain.queue.domain.QueueToken;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;


@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

	@InjectMocks
	private QueueService queueService;

	@Mock
	private QueueApplicationService queueApplicationService;

	@Mock
	private DistributedLockAspect distributedLockAspect;

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
	void issueQueueToken_Success_Waiting() throws Exception {
		QueueToken waitingToken = QueueToken.waitingTokenOf(tokenId, userId, concertId, 10);
		String expectedLockKey = "queue:" + concertId;

		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<QueueToken> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(queueApplicationService.processIssueQueueToken(userId, concertId)).thenReturn(waitingToken);

		QueueToken queueToken = queueService.issueQueueToken(userId, concertId);

		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(queueApplicationService, times(1)).processIssueQueueToken(userId, concertId);

		assertThat(queueToken.status()).isEqualTo(QueueStatus.WAITING);
		assertThat(queueToken.position()).isEqualTo(11);
		assertThat(queueToken.issuedAt()).isNotNull();
		assertThat(queueToken.expiresAt()).isNull();
		assertThat(queueToken.enteredAt()).isNull();
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_성공(활성상태)") // 최대 활성 토큰 수 50개
	void issueQueueToken_Success_Active() throws Exception {
		QueueToken activeToken = QueueToken.activeTokenOf(tokenId, userId, concertId, 1800000L);
		String expectedLockKey = "queue:" + concertId;

		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<QueueToken> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(queueApplicationService.processIssueQueueToken(userId, concertId)).thenReturn(activeToken);

		QueueToken queueToken = queueService.issueQueueToken(userId, concertId);

		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(queueApplicationService, times(1)).processIssueQueueToken(userId, concertId);

		assertThat(queueToken.status()).isEqualTo(QueueStatus.ACTIVE);
		assertThat(queueToken.issuedAt()).isNotNull();
		assertThat(queueToken.expiresAt()).isNotNull();
		assertThat(queueToken.enteredAt()).isNotNull();
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_성공(기존_토큰_존재)")
	void issueQueueToken_Success_existsToken() throws Exception {
		String expectedLockKey = "queue:" + concertId;

		// 분산락 Mock 설정
		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<QueueToken> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(queueApplicationService.processIssueQueueToken(userId, concertId)).thenReturn(existingToken);

		QueueToken queueToken = queueService.issueQueueToken(userId, concertId);

		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(queueApplicationService, times(1)).processIssueQueueToken(userId, concertId);

		assertThat(queueToken.tokenId()).isEqualTo(tokenId);
		assertThat(queueToken.status()).isEqualTo(QueueStatus.ACTIVE);
		assertThat(queueToken.issuedAt()).isNotNull();
		assertThat(queueToken.expiresAt()).isNotNull();
		assertThat(queueToken.enteredAt()).isNotNull();
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_실패_유저못찾음")
	void issueQueueToken_Failure_UserNotFound() throws Exception {
		String expectedLockKey = "queue:" + concertId;

		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<QueueToken> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(queueApplicationService.processIssueQueueToken(userId, concertId))
			.thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		CustomException customException = assertThrows(CustomException.class,
			() -> queueService.issueQueueToken(userId, concertId));

		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(queueApplicationService, times(1)).processIssueQueueToken(userId, concertId);

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_발급_실패_콘서트못찾음")
	void issueQueueToken_Failure_ConcertNotFound() throws Exception {
		String expectedLockKey = "queue:" + concertId;

		// 분산락 Mock 설정 - 내부에서 예외 발생
		when(distributedLockAspect.executeWithLockHasReturn(eq(expectedLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<QueueToken> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(queueApplicationService.processIssueQueueToken(userId, concertId))
			.thenThrow(new CustomException(ErrorCode.CONCERT_NOT_FOUND));

		CustomException customException = assertThrows(CustomException.class,
			() -> queueService.issueQueueToken(userId, concertId));

		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(expectedLockKey), any());
		verify(queueApplicationService, times(1)).processIssueQueueToken(userId, concertId);

		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CONCERT_NOT_FOUND);
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_조회_성공")
	void getQueueInfo_Success() throws CustomException {
		String tokenIdString = tokenId.toString();
		
		when(queueApplicationService.getQueueInfo(concertId, tokenIdString)).thenReturn(existingToken);

		QueueToken queueToken = queueService.getQueueInfo(concertId, tokenIdString);

		verify(queueApplicationService, times(1)).getQueueInfo(concertId, tokenIdString);
		
		assertThat(queueToken.tokenId()).isEqualTo(tokenId);
		assertThat(queueToken.status()).isEqualTo(QueueStatus.ACTIVE);
		assertThat(queueToken.userId()).isEqualTo(userId);
		assertThat(queueToken.concertId()).isEqualTo(concertId);
	}

	@Test
	@DisplayName("콘서트_대기열_토큰_조회_실패_토큰못찾음")
	void getQueueInfo_Failure_TokenNotFound() throws CustomException {
		String tokenIdString = tokenId.toString();
		
		when(queueApplicationService.getQueueInfo(concertId, tokenIdString))
			.thenThrow(new CustomException(ErrorCode.INVALID_QUEUE_TOKEN));

		CustomException customException = assertThrows(CustomException.class,
			() -> queueService.getQueueInfo(concertId, tokenIdString));

		verify(queueApplicationService, times(1)).getQueueInfo(concertId, tokenIdString);
		
		assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_QUEUE_TOKEN);
	}
}