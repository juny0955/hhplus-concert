package kr.hhplus.be.server.usecase.reservation.interactor;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import kr.hhplus.be.server.common.aop.DistributedLockAspect;
import kr.hhplus.be.server.domain.reservation.domain.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.reservation.dto.CreateReservationResult;
import kr.hhplus.be.server.domain.reservation.usecase.CreateReservationService;
import kr.hhplus.be.server.domain.reservation.port.in.ReserveSeatCommand;
import kr.hhplus.be.server.reservation.usecase.output.ReservationOutput;
import kr.hhplus.be.server.domain.reservation.dto.ReserveSeatResult;

@ExtendWith(MockitoExtension.class)
public class ReservationDistributedLockTest {

	@InjectMocks
	private CreateReservationService createReservationService;

	@Mock
	private CreateReservationManager createReservationManager;

	@Mock
	private ReservationOutput reservationOutput;

	@Mock
	private EventPublisher eventPublisher;

	@Mock
	private DistributedLockAspect distributedLockAspect;

	private ReserveSeatCommand reserveSeatCommand;
	private CreateReservationResult createReservationResult;

	private String seatLockKey;
	private UUID concertId;
	private UUID concertDateId;
	private UUID seatId;
	private UUID userId;
	private String queueTokenId;

	@BeforeEach
	void beforeEach() {
		concertId = UUID.randomUUID();
		concertDateId = UUID.randomUUID();
		seatId = UUID.randomUUID();
		userId = UUID.randomUUID();
		queueTokenId = UUID.randomUUID().toString();

		seatLockKey = "reserve:seat:" + seatId;

		reserveSeatCommand = new ReserveSeatCommand(concertId, concertDateId, seatId, queueTokenId);
		
		Reservation reservation = mock(Reservation.class);
		Payment payment = mock(Payment.class);
		Seat seat = mock(Seat.class);
		createReservationResult = new CreateReservationResult(reservation, payment, seat, userId);
	}

	@Test
	@DisplayName("Seat 락을 획득하지 못하면 트랜잭션을 실행하지 않는다")
	void NotHasSeatLock() throws Exception {
		when(distributedLockAspect.executeWithSimpleLockHasReturn(eq(seatLockKey), any()))
			.thenThrow(new CustomException(ErrorCode.LOCK_CONFLICT));

		CustomException exception = assertThrows(CustomException.class,
			() -> createReservationService.createReservation(reserveSeatCommand));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LOCK_CONFLICT);

		verify(distributedLockAspect, times(1)).executeWithSimpleLockHasReturn(eq(seatLockKey), any());
		verify(createReservationManager, never()).processCreateReservation(any());
		verify(eventPublisher, never()).publish(any());
		verify(reservationOutput, never()).ok(any());
	}

	@Test
	@DisplayName("Seat 락 획득시 예약 트랜잭션을 수행한다")
	void GetSeatLock() throws Exception {
		when(distributedLockAspect.executeWithSimpleLockHasReturn(eq(seatLockKey), any()))
			.thenAnswer(invocation -> {
				Callable<CreateReservationResult> callable = invocation.getArgument(1);
				return callable.call();
			});
		when(createReservationManager.processCreateReservation(reserveSeatCommand)).thenReturn(createReservationResult);

		createReservationService.createReservation(reserveSeatCommand);

		verify(distributedLockAspect, times(1)).executeWithSimpleLockHasReturn(eq(seatLockKey), any());
		verify(createReservationManager, times(1)).processCreateReservation(reserveSeatCommand);
		verify(eventPublisher, times(1)).publish(any(ReservationCreatedEvent.class));
		verify(reservationOutput, times(1)).ok(any(ReserveSeatResult.class));
	}

	@Test
	@DisplayName("동일한 좌석에 동시 예약 요청시 하나만 성공한다")
	void ConcurrentSeatReservation() throws Exception {
		int threadCount = 5;
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger lockConflictCount = new AtomicInteger(0);

		when(distributedLockAspect.executeWithSimpleLockHasReturn(eq(seatLockKey), any()))
			.thenAnswer(invocation -> {
				if (successCount.get() == 0) {
					successCount.incrementAndGet();
					Callable<CreateReservationResult> callable = invocation.getArgument(1);
					return callable.call();
				} else {
					throw new CustomException(ErrorCode.LOCK_CONFLICT);
				}
			});
		when(createReservationManager.processCreateReservation(any())).thenReturn(createReservationResult);

		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					createReservationService.createReservation(reserveSeatCommand);
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
	@DisplayName("다른 좌석에 동시 예약 요청시 모두 성공한다")
	void ConcurrentDifferentSeatReservation() throws Exception {
		int threadCount = 3;
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);

		List<ReserveSeatCommand> commands = new ArrayList<>();
		List<String> lockKeys = new ArrayList<>();
		
		for (int i = 0; i < threadCount; i++) {
			UUID differentSeatId = UUID.randomUUID();
			ReserveSeatCommand command = new ReserveSeatCommand(concertId, concertDateId, differentSeatId, queueTokenId);
			String lockKey = "reserve:seat:" + differentSeatId;
			
			commands.add(command);
			lockKeys.add(lockKey);
			
			when(distributedLockAspect.executeWithSimpleLockHasReturn(eq(lockKey), any()))
				.thenAnswer(invocation -> {
					successCount.incrementAndGet();
					Callable<CreateReservationResult> callable = invocation.getArgument(1);
					return callable.call();
				});
		}
		
		when(createReservationManager.processCreateReservation(any())).thenReturn(createReservationResult);

		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			final int index = i;
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					createReservationService.createReservation(commands.get(index));
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
		
		for (String lockKey : lockKeys)
			verify(distributedLockAspect, times(1)).executeWithSimpleLockHasReturn(eq(lockKey), any());
	}
}