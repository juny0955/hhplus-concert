package kr.hhplus.be.server.concert.adapters.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.reservation.ports.in.ReservationExpiredInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpiredSeatHoldScheduler {

    private final ReservationExpiredInput reservationExpiredInput;

    @Scheduled(fixedRate = 60000)
    public void checkExpiredSeatHold() throws Exception {
        reservationExpiredInput.expiredReservation();
    }
}
