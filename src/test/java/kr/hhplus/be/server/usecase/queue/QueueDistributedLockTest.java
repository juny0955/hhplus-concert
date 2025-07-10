package kr.hhplus.be.server.usecase.queue;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.common.aop.DistributedLockAspect;
import kr.hhplus.be.server.queue.adapter.out.persistence.QueueApplicationService;
import kr.hhplus.be.server.reservation.domain.queue.QueueToken;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.queue.usecase.QueueService;

@ExtendWith(MockitoExtension.class)
public class QueueDistributedLockTest {

	@InjectMocks
	private QueueService queueService;

	@Mock
	private QueueApplicationService queueApplicationService;

	@Mock
	private DistributedLockAspect distributedLockAspect;

	private QueueToken activeToken;
	private UUID userId;
	private UUID concertId;
	private String queueLockKey;

	@BeforeEach
	void beforeEach() {
		userId = UUID.randomUUID();
		concertId = UUID.randomUUID();
		queueLockKey = "queue:issue:" + concertId;

		activeToken = QueueToken.activeTokenOf(UUID.randomUUID(), userId, concertId, 60L);
	}

	@Test
	@DisplayName("Queue 락을 획득하지 못하면 토큰 발급을 실행하지 않는다")
	void NotHasQueueLock() throws Exception {
		when(distributedLockAspect.executeWithLockHasReturn(eq(queueLockKey), any()))
			.thenThrow(new CustomException(ErrorCode.LOCK_CONFLICT));

		CustomException exception = assertThrows(CustomException.class,
			() -> queueService.issueQueueToken(userId, concertId));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LOCK_CONFLICT);

		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(queueLockKey), any());
		verify(queueApplicationService, never()).processIssueQueueToken(any(), any());
	}

	@Test
	@DisplayName("Queue 락 획득시 토큰 발급을 수행한다")
	void GetQueueLock() throws Exception {
		when(distributedLockAspect.executeWithLockHasReturn(eq(queueLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<QueueToken> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(queueApplicationService.processIssueQueueToken(userId, concertId)).thenReturn(activeToken);

		QueueToken result = queueService.issueQueueToken(userId, concertId);

		assertThat(result).isEqualTo(activeToken);
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(queueLockKey), any());
		verify(queueApplicationService, times(1)).processIssueQueueToken(userId, concertId);
	}

	@Test
	@DisplayName("동일한 콘서트에 동시 토큰 발급 요청시 순차 처리된다")
	void ConcurrentTokenIssueSameConcert() throws Exception {
		int threadCount = 10;
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger lockConflictCount = new AtomicInteger(0);

		// 첫 번째 요청만 성공, 나머지는 락 충돌
		when(distributedLockAspect.executeWithLockHasReturn(eq(queueLockKey), any()))
			.thenAnswer(invocation -> {
				if (successCount.get() == 0) {
					successCount.incrementAndGet();
					Callable<QueueToken> callable = invocation.getArgument(1);
					return callable.call();
				} else {
					throw new CustomException(ErrorCode.LOCK_CONFLICT);
				}
			});
		when(queueApplicationService.processIssueQueueToken(userId, concertId)).thenReturn(activeToken);

		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					queueService.issueQueueToken(userId, concertId);
				} catch (CustomException e) {
					if (e.getErrorCode() == ErrorCode.LOCK_CONFLICT) {
						lockConflictCount.incrementAndGet();
					}
				} catch (Exception e) {
					// 다른 예외 무시
				} finally {
					latch.countDown();
				}
			});
			futures.add(future);
		}

		latch.await();
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		assertThat(successCount.get()).isEqualTo(1);
		assertThat(lockConflictCount.get()).isEqualTo(threadCount - 1);
	}

	@Test
	@DisplayName("다른 콘서트에 동시 토큰 발급 요청시 모두 성공한다")
	void ConcurrentTokenIssueDifferentConcerts() throws Exception {
		int threadCount = 3;
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);

		List<UUID> concertIds = new ArrayList<>();
		List<String> lockKeys = new ArrayList<>();
		List<QueueToken> tokens = new ArrayList<>();

		for (int i = 0; i < threadCount; i++) {
			UUID differentConcertId = UUID.randomUUID();
			String lockKey = "queue:issue:" + differentConcertId;
			QueueToken token = QueueToken.activeTokenOf(UUID.randomUUID(), userId, differentConcertId, 60L);

			concertIds.add(differentConcertId);
			lockKeys.add(lockKey);
			tokens.add(token);

			when(distributedLockAspect.executeWithLockHasReturn(eq(lockKey), any()))
				.thenAnswer(invocation -> {
					successCount.incrementAndGet();
					Callable<QueueToken> callable = invocation.getArgument(1);
					return callable.call();
				});
			when(queueApplicationService.processIssueQueueToken(userId, differentConcertId)).thenReturn(token);
		}

		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			final int index = i;
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					queueService.issueQueueToken(userId, concertIds.get(index));
				} catch (Exception e) {

				} finally {
					latch.countDown();
				}
			});
			futures.add(future);
		}

		latch.await();
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		assertThat(successCount.get()).isEqualTo(threadCount);

		for (int i = 0; i < threadCount; i++) {
			verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(lockKeys.get(i)), any());
			verify(queueApplicationService, times(1)).processIssueQueueToken(userId, concertIds.get(i));
		}
	}

}