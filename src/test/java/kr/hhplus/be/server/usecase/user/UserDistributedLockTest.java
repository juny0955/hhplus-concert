package kr.hhplus.be.server.usecase.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.infrastructure.persistence.user.UserManager;

@ExtendWith(MockitoExtension.class)
public class UserDistributedLockTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserManager userManager;

	@Mock
	private DistributedLockManager distributedLockManager;

	private User user;
	private UUID userId;
	private BigDecimal chargePoint;
	private String userLockKey;

	@BeforeEach
	void beforeEach() {
		userId = UUID.randomUUID();
		chargePoint = BigDecimal.valueOf(5000);
		userLockKey = "user:" + userId;

		user = User.builder()
			.id(userId)
			.amount(BigDecimal.valueOf(10000))
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();
	}

	@Test
	@DisplayName("User 락을 획득하지 못하면 포인트 충전을 실행하지 않는다")
	void NotHasUserLock() throws Exception {
		when(distributedLockManager.executeWithLockHasReturn(eq(userLockKey), any()))
			.thenThrow(new CustomException(ErrorCode.LOCK_CONFLICT));

		CustomException exception = assertThrows(CustomException.class,
			() -> userService.chargePoint(userId, chargePoint));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LOCK_CONFLICT);

		verify(distributedLockManager, times(1)).executeWithLockHasReturn(eq(userLockKey), any());
		verify(userManager, never()).chargePoint(any(), any());
	}

	@Test
	@DisplayName("User 락 획득시 포인트 충전을 수행한다")
	void GetUserLock() throws Exception {
		when(distributedLockManager.executeWithLockHasReturn(eq(userLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<User> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(userManager.chargePoint(userId, chargePoint)).thenReturn(user);

		User result = userService.chargePoint(userId, chargePoint);

		assertThat(result).isEqualTo(user);
		verify(distributedLockManager, times(1)).executeWithLockHasReturn(eq(userLockKey), any());
		verify(userManager, times(1)).chargePoint(userId, chargePoint);
	}

	@Test
	@DisplayName("최소 충전 금액 미만시 예외가 발생한다")
	void NotEnoughMinChargePoint() throws Exception {
		BigDecimal invalidPoint = BigDecimal.valueOf(500); // 최소 충전 금액 1000원 미만

		CustomException exception = assertThrows(CustomException.class,
			() -> userService.chargePoint(userId, invalidPoint));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT);

		verify(distributedLockManager, never()).executeWithLockHasReturn(anyString(), any());
		verify(userManager, never()).chargePoint(any(), any());
	}

	@Test
	@DisplayName("동시에 포인트 충전시 하나만 성공한다")
	void ConcurrentChargePointSameUser() throws Exception {
		int threadCount = 5;
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger lockConflictCount = new AtomicInteger(0);

		when(distributedLockManager.executeWithLockHasReturn(eq(userLockKey), any()))
			.thenAnswer(invocation -> {
				if (successCount.get() == 0) {
					successCount.incrementAndGet();
					Callable<User> callable = invocation.getArgument(1);
					return callable.call();
				} else {
					throw new CustomException(ErrorCode.LOCK_CONFLICT);
				}
			});
		when(userManager.chargePoint(userId, chargePoint)).thenReturn(user);

		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					userService.chargePoint(userId, chargePoint);
				} catch (CustomException e) {
					if (e.getErrorCode() == ErrorCode.LOCK_CONFLICT) {
						lockConflictCount.incrementAndGet();
					}
				} catch (Exception e) {

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
	@DisplayName("다른 사용자가 동시에 포인트 충전시 모두 성공한다")
	void ConcurrentChargePointDifferentUsers() throws Exception {
		int threadCount = 3;
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);

		List<UUID> userIds = new ArrayList<>();
		List<String> lockKeys = new ArrayList<>();
		List<User> users = new ArrayList<>();

		for (int i = 0; i < threadCount; i++) {
			UUID differentUserId = UUID.randomUUID();
			String lockKey = "user:" + differentUserId;
			User differentUser = User.builder()
				.id(differentUserId)
				.amount(BigDecimal.valueOf(10000))
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

			userIds.add(differentUserId);
			lockKeys.add(lockKey);
			users.add(differentUser);

			when(distributedLockManager.executeWithLockHasReturn(eq(lockKey), any()))
				.thenAnswer(invocation -> {
					successCount.incrementAndGet();
					Callable<User> callable = invocation.getArgument(1);
					return callable.call();
				});
			when(userManager.chargePoint(differentUserId, chargePoint)).thenReturn(differentUser);
		}

		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			final int index = i;
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					userService.chargePoint(userIds.get(index), chargePoint);
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
			verify(distributedLockManager, times(1)).executeWithLockHasReturn(eq(lockKeys.get(i)), any());
			verify(userManager, times(1)).chargePoint(userIds.get(i), chargePoint);
		}
	}

}