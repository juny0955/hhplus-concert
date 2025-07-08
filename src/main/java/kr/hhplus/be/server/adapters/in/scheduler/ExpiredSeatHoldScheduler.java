package kr.hhplus.be.server.adapters.in.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.reservation.port.in.ReservationExpireInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpiredSeatHoldScheduler {

    private final ReservationExpireInput reservationExpireInput;

    @Scheduled(fixedRate = 60000)
    public void checkExpiredSeatHold() throws Exception {
        reservationExpireInput.expiredReservation();
    }
}
