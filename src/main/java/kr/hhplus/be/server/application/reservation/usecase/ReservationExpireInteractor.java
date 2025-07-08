package kr.hhplus.be.server.application.reservation.usecase;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.adapters.out.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.application.reservation.dto.ExpiredReservationResult;
import kr.hhplus.be.server.application.reservation.port.in.ReservationExpireInput;
import kr.hhplus.be.server.application.reservation.service.ReservationService;
import kr.hhplus.be.server.application.seatHold.port.in.CheckSeatHoldInput;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationExpiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpireInteractor implements ReservationExpireInput {

    private final static String SCHEDULER_LOCK_KEY = "scheduler:reservation-expired";
    private final static String RESERVATION_LOCK_KEY = "reservation:";

    private final ReservationService reservationService;
    private final CheckSeatHoldInput checkSeatHoldInput;
    private final ApplicationEventPublisher eventPublisher;
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
        List<Reservation> reservations = reservationService.getPendingReservations();

        for (Reservation reservation : reservations) {
            String lockKey = RESERVATION_LOCK_KEY + reservation.id();
            checkSeatHoldInput.checkSeatHold(reservation.seatId(), reservation.userId());

            // reservation:{reservationId} 락 획득 후 임시배정 만료 트랜잭션 수행
            ExpiredReservationResult expiredReservationResult = distributedLockManager.executeWithLockHasReturn(
                lockKey,
                () -> reservationService.expireReservation(reservation)
            );

            if (expiredReservationResult == null)
                continue;

            eventPublisher.publishEvent(ReservationExpiredEvent.from(expiredReservationResult));
        }
    }
}
