package kr.hhplus.be.server.domain.seatHold.adapter.in.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.reservation.port.in.ReservationExpireInput;
import kr.hhplus.be.server.common.aop.DistributedLock;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpiredSeatHoldScheduler {

    private final ReservationExpireInput reservationExpireInput;

    @Scheduled(fixedRate = 60000)
    @DistributedLock(key = "scheduler:reservation-expired")
    public void checkExpiredSeatHold() throws Exception {
        reservationExpireInput.expiredReservation();
    }
}
