package kr.hhplus.be.server.application.reservation.usecase;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

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
public class ReservationExpireService implements ReservationExpireInput {

    private final ReservationService reservationService;
    private final CheckSeatHoldInput checkSeatHoldInput;
    private final ApplicationEventPublisher eventPublisher;

	@Override
    public void expiredReservation() {
        try {
            List<Reservation> reservations = reservationService.getPendingReservations();

            for (Reservation reservation : reservations) {
                checkSeatHoldInput.checkSeatHold(reservation.seatId(), reservation.userId());

                ExpiredReservationResult expiredReservationResult = reservationService.expireReservation(reservation);

                if (expiredReservationResult == null)
                    continue;

                eventPublisher.publishEvent(ReservationExpiredEvent.from(expiredReservationResult));
            }
        } catch (Exception e) {
            log.error("예약 만료 처리 스케줄러 처리 중 예외발생", e);
        }
    }
}
