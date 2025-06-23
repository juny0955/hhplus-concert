package kr.hhplus.be.server.usecase.reservation.interactor;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.event.reservation.ReservationExpiredEvent;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.infrastructure.persistence.reservation.ExpiredReservationManager;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.reservation.input.ReservationExpiredInput;
import kr.hhplus.be.server.infrastructure.persistence.reservation.ExpiredReservationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiredInteractor implements ReservationExpiredInput {

    private final ExpiredReservationManager expiredReservationManager;
    private final EventPublisher eventPublisher;

    @Override
    public void expiredReservation() {
        try {
            List<Reservation> reservations = expiredReservationManager.getPendingReservations();

            for (Reservation reservation : reservations) {
                ExpiredReservationResult createReservationResult = expiredReservationManager.processExpiredReservation(reservation);

                if (createReservationResult == null)
                    continue;

                eventPublisher.publish(ReservationExpiredEvent.from(createReservationResult));
            }
        } catch (CustomException e) {
            ErrorCode errorCode = e.getErrorCode();
            log.warn("임시배정 스케줄러 동작 중 비즈니스 예외 발생 - {}, {}", errorCode.getCode(), errorCode.getMessage());
        } catch (Exception e) {
            log.error("임시배정 스케줄러 동작중 예외 발생", e);
        }
    }
}
