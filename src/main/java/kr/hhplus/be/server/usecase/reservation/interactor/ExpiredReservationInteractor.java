package kr.hhplus.be.server.usecase.reservation.interactor;

import kr.hhplus.be.server.domain.event.reservation.ReservationExpiredEvent;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.usecase.event.EventPublisher;
import kr.hhplus.be.server.usecase.reservation.input.ReservationExpiredInput;
import kr.hhplus.be.server.usecase.reservation.service.ExpiredReservationService;
import kr.hhplus.be.server.usecase.reservation.service.CreateReservationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiredReservationInteractor implements ReservationExpiredInput {

    private final ExpiredReservationService expiredReservationService;
    private final EventPublisher eventPublisher;

    @Override
    public void expiredReservation() {
        try {
            // TODO 개별 Reservation 트랜잭션 적용해야함 분리 어떻게 해야할지
            List<CreateReservationResult> createReservationResults = expiredReservationService.processExpiredReservation();

            createReservationResults.forEach(reservationTransactionResult -> eventPublisher.publish(ReservationExpiredEvent.from(reservationTransactionResult)));
        } catch (CustomException e) {
            ErrorCode errorCode = e.getErrorCode();
            log.warn("임시배정 스케줄러 동작 중 비즈니스 예외 발생 - {}, {}", errorCode.getCode(), errorCode.getMessage());
        } catch (Exception e) {
            log.error("임시배정 스케줄러 동작중 예외 발생", e);
        }
    }
}
