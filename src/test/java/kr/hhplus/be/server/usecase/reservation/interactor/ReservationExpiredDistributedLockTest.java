package kr.hhplus.be.server.usecase.reservation.interactor;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
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
import org.springframework.context.ApplicationEventPublisher;

import kr.hhplus.be.server.common.aop.DistributedLockAspect;
import kr.hhplus.be.server.domain.reservation.domain.ReservationExpiredEvent;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.reservation.domain.ReservationStatus;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.reservation.dto.ExpiredReservationResult;
import kr.hhplus.be.server.domain.reservation.usecase.ExpireReservationService;

@ExtendWith(MockitoExtension.class)
public class ReservationExpiredDistributedLockTest {

	@InjectMocks
	private ExpireReservationService reservationExpireService;

	@Mock
	private ExpiredReservationManager expiredReservationManager;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private DistributedLockAspect distributedLockAspect;

	private List<Reservation> expiredReservations;
	private ExpiredReservationResult expiredReservationResult;

	private String schedulerLockKey;
	private String reservationLockKey;

	@BeforeEach
	void beforeEach() {
		schedulerLockKey = "scheduler:reservation-cancel";
		
		UUID reservationId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID seatId = UUID.randomUUID();
		UUID paymentId = UUID.randomUUID();
		
		reservationLockKey = "reservation:" + reservationId;
		
		Reservation expiredReservation = Reservation.builder()
			.id(reservationId)
			.userId(userId)
			.seatId(seatId)
			.status(ReservationStatus.PENDING)
			.createdAt(LocalDateTime.now().minusMinutes(10))
			.updatedAt(LocalDateTime.now().minusMinutes(5))
			.build();
		
		expiredReservations = List.of(expiredReservation);
		expiredReservationResult = ExpiredReservationResult.from(reservationId, paymentId, seatId, userId);
	}

	@Test
	@DisplayName("스케줄러 락을 획득하지 못하면 예약 만료 처리를 실행하지 않는다")
	void NotHasSchedulerLock() throws Exception {
		doThrow(new CustomException(ErrorCode.LOCK_CONFLICT))
			.when(distributedLockAspect).executeWithLock(eq(schedulerLockKey), any());

		CustomException exception = assertThrows(CustomException.class,
			() -> reservationExpireService.expireReservation());

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LOCK_CONFLICT);

		verify(distributedLockAspect, times(1)).executeWithLock(eq(schedulerLockKey), any());
		verify(expiredReservationManager, never()).getPendingReservations();
		verify(distributedLockAspect, never()).executeWithLockHasReturn(anyString(), any());
		verify(eventPublisher, never()).publishEvent(any());
	}

	@Test
	@DisplayName("스케줄러 락 획득시 예약 만료 처리를 수행한다")
	void GetSchedulerLock() throws Exception {
		doAnswer(invocation -> {
			Runnable runnable = invocation.getArgument(1);
			runnable.run();
			return null;
		}).when(distributedLockAspect).executeWithLock(eq(schedulerLockKey), any());

		when(expiredReservationManager.getPendingReservations()).thenReturn(expiredReservations);
		when(distributedLockAspect.executeWithLockHasReturn(eq(reservationLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<ExpiredReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(expiredReservationManager.processExpiredReservation(any())).thenReturn(expiredReservationResult);

		reservationExpireService.expireReservation();

		verify(distributedLockAspect, times(1)).executeWithLock(eq(schedulerLockKey), any());
		verify(expiredReservationManager, times(1)).getPendingReservations();
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(reservationLockKey), any());
		verify(expiredReservationManager, times(1)).processExpiredReservation(any());
		verify(eventPublisher, times(1)).publishEvent(any(ReservationExpiredEvent.class));
	}

	@Test
	@DisplayName("예약 락을 획득하지 못하면 해당 예약은 건너뛴다")
	void NotHasReservationLock() throws Exception {
		doAnswer(invocation -> {
			Runnable runnable = invocation.getArgument(1);
			runnable.run();
			return null;
		}).when(distributedLockAspect).executeWithLock(eq(schedulerLockKey), any());

		when(expiredReservationManager.getPendingReservations()).thenReturn(expiredReservations);
		when(distributedLockAspect.executeWithLockHasReturn(eq(reservationLockKey), any()))
			.thenThrow(new CustomException(ErrorCode.LOCK_CONFLICT));

		assertDoesNotThrow(() -> reservationExpireService.expireReservation());

		verify(distributedLockAspect, times(1)).executeWithLock(eq(schedulerLockKey), any());
		verify(expiredReservationManager, times(1)).getPendingReservations();
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(reservationLockKey), any());
		verify(expiredReservationManager, never()).processExpiredReservation(any());
		verify(eventPublisher, never()).publishEvent(any());
	}

	@Test
	@DisplayName("여러 예약 처리시 개별 예약마다 락을 획득한다")
	void MultipleReservationsWithIndividualLocks() throws Exception {
		UUID reservationId1 = UUID.randomUUID();
		UUID reservationId2 = UUID.randomUUID();
		UUID reservationId3 = UUID.randomUUID();
		
		List<Reservation> reservations = List.of(
			Reservation.builder()
				.id(reservationId1)
				.userId(UUID.randomUUID())
				.seatId(UUID.randomUUID())
				.status(ReservationStatus.PENDING)
				.createdAt(LocalDateTime.now().minusMinutes(10))
				.updatedAt(LocalDateTime.now().minusMinutes(5))
				.build(),
			Reservation.builder()
				.id(reservationId2)
				.userId(UUID.randomUUID())
				.seatId(UUID.randomUUID())
				.status(ReservationStatus.PENDING)
				.createdAt(LocalDateTime.now().minusMinutes(10))
				.updatedAt(LocalDateTime.now().minusMinutes(5))
				.build(),
			Reservation.builder()
				.id(reservationId3)
				.userId(UUID.randomUUID())
				.seatId(UUID.randomUUID())
				.status(ReservationStatus.PENDING)
				.createdAt(LocalDateTime.now().minusMinutes(10))
				.updatedAt(LocalDateTime.now().minusMinutes(5))
				.build()
		);

		doAnswer(invocation -> {
			Runnable runnable = invocation.getArgument(1);
			runnable.run();
			return null;
		}).when(distributedLockAspect).executeWithLock(eq(schedulerLockKey), any());

		when(expiredReservationManager.getPendingReservations()).thenReturn(reservations);
		when(distributedLockAspect.executeWithLockHasReturn(anyString(), any()))
			.thenAnswer(invocation -> {
				Callable<ExpiredReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(expiredReservationManager.processExpiredReservation(any())).thenReturn(expiredReservationResult);

		reservationExpireService.expireReservation();

		verify(distributedLockAspect, times(1)).executeWithLock(eq(schedulerLockKey), any());
		verify(expiredReservationManager, times(1)).getPendingReservations();
		verify(distributedLockAspect, times(3)).executeWithLockHasReturn(anyString(), any());
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq("reservation:" + reservationId1), any());
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq("reservation:" + reservationId2), any());
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq("reservation:" + reservationId3), any());
		verify(expiredReservationManager, times(3)).processExpiredReservation(any());
		verify(eventPublisher, times(3)).publishEvent(any(ReservationExpiredEvent.class));
	}

	@Test
	@DisplayName("동시에 스케줄러가 실행되면 하나만 성공한다")
	void ConcurrentSchedulerExecution() throws Exception {
		int threadCount = 5;
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger lockConflictCount = new AtomicInteger(0);

		doAnswer(invocation -> {
			if (successCount.get() == 0) {
				successCount.incrementAndGet();
				Runnable runnable = invocation.getArgument(1);
				runnable.run();
			} else {
				throw new CustomException(ErrorCode.LOCK_CONFLICT);
			}
			return null;
		}).when(distributedLockAspect).executeWithLock(eq(schedulerLockKey), any());

		when(expiredReservationManager.getPendingReservations()).thenReturn(expiredReservations);
		when(distributedLockAspect.executeWithLockHasReturn(eq(reservationLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<ExpiredReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(expiredReservationManager.processExpiredReservation(any())).thenReturn(expiredReservationResult);

		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					reservationExpireService.expireReservation();
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
	@DisplayName("예약 만료 처리가 null을 반환하면 이벤트를 발행하지 않는다")
	void ProcessExpiredReservationReturnsNull() throws Exception {
		doAnswer(invocation -> {
			Runnable runnable = invocation.getArgument(1);
			runnable.run();
			return null;
		}).when(distributedLockAspect).executeWithLock(eq(schedulerLockKey), any());

		when(expiredReservationManager.getPendingReservations()).thenReturn(expiredReservations);
		when(distributedLockAspect.executeWithLockHasReturn(eq(reservationLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<ExpiredReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(expiredReservationManager.processExpiredReservation(any())).thenReturn(null);

		reservationExpireService.expireReservation();

		verify(distributedLockAspect, times(1)).executeWithLock(eq(schedulerLockKey), any());
		verify(expiredReservationManager, times(1)).getPendingReservations();
		verify(distributedLockAspect, times(1)).executeWithLockHasReturn(eq(reservationLockKey), any());
		verify(expiredReservationManager, times(1)).processExpiredReservation(any());
		verify(eventPublisher, never()).publishEvent(any());
	}

}