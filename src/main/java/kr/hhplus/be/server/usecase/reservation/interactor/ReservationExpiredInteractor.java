package kr.hhplus.be.server.usecase.reservation.interactor;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.event.reservation.ReservationExpiredEvent;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.infrastructure.persistence.reservation.ExpiredReservationManager;
import kr.hhplus.be.server.infrastructure.persistence.reservation.ExpiredReservationResult;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.reservation.input.ReservationExpiredInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiredInteractor implements ReservationExpiredInput {

    private final static String SCHEDULER_LOCK_KEY = "scheduler:reservation-expired";
    private final static String RESERVATION_LOCK_KEY = "reservation:";

    private final ExpiredReservationManager expiredReservationManager;
    private final EventPublisher eventPublisher;
    private final DistributedLockManager distributedLockManager;

	@Override
    public void expiredReservation() throws Exception {
        // scheduler:reservation-expired 락 획득 후 스케줄러 동작
        distributedLockManager.executeWithLock(
            SCHEDULER_LOCK_KEY,
            () -> {
                try {
                    process();
                } catch (Exception e) {
                    log.error("예약 만료 처리 스케줄러 처리 중 예외발생", e);
                }
            });
    }

    private void process() throws Exception {
        List<Reservation> reservations = expiredReservationManager.getPendingReservations();

        for (Reservation reservation : reservations) {
            String lockKey = RESERVATION_LOCK_KEY + reservation.id();

            // reservation:{reservationId} 락 획득 후 임시배정 만료 트랜잭션 수행
            ExpiredReservationResult expiredReservationResult = distributedLockManager.executeWithLockHasReturn(
                lockKey,
                () -> expiredReservationManager.processExpiredReservation(reservation)
            );

            if (expiredReservationResult == null)
                continue;

            eventPublisher.publish(ReservationExpiredEvent.from(expiredReservationResult));
        }
    }
}
