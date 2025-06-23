package kr.hhplus.be.server.infrastructure.scheduler;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.usecase.reservation.input.ReservationExpiredInput;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiredSeatHoldScheduler {

    private final ReservationExpiredInput reservationExpiredInput;

    @Scheduled(fixedRate = 60000)
    public void checkExpiredSeatHold() throws CustomException {
        reservationExpiredInput.expiredReservation();
    }
}
