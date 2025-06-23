package kr.hhplus.be.server.infrastructure.scheduler;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.usecase.reservation.input.ReservationInput;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiredSeatHoldScheduler {

    private final ReservationInput reservationInput;

    @Scheduled(fixedRate = 60000)
    public void checkExpiredSeatHold() throws CustomException {
        reservationInput.expiredReservation();
    }
}
