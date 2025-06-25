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

    private final static String LOCK_KEY = "reservation:";

    private final ExpiredReservationManager expiredReservationManager;
    private final EventPublisher eventPublisher;
    private final DistributedLockManager distributedLockManager;

    @Override
    public void expiredReservation() throws Exception {
        List<Reservation> reservations = expiredReservationManager.getPendingReservations();

        for (Reservation reservation : reservations) {
            String lockKey = LOCK_KEY + reservation.id();

            ExpiredReservationResult expiredReservationResult = distributedLockManager.executeWithLock(
                lockKey,
                () -> expiredReservationManager.processExpiredReservation(reservation)
            );

            if (expiredReservationResult == null)
                continue;

            eventPublisher.publish(ReservationExpiredEvent.from(expiredReservationResult));
        }
    }
}
