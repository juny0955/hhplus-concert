package kr.hhplus.be.server.reservation.adapter.in.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.reservation.port.in.reservation.ExpireReservationUseCase;
import kr.hhplus.be.server.common.aop.DistributedLock;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpiredSeatHoldScheduler {

    private final ExpireReservationUseCase expireReservationUseCase;

    @Scheduled(fixedRate = 60000)
    @DistributedLock(key = "scheduler:reservation-cancel")
    public void checkExpiredSeatHold() throws Exception {
        expireReservationUseCase.expireReservation();
    }
}
