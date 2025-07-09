package kr.hhplus.be.server.domain.reservation.usecase;

import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.reservation.manager.ExpireReservationManager;
import kr.hhplus.be.server.domain.reservation.port.in.ExpireReservationUseCase;
import kr.hhplus.be.server.domain.reservation.port.out.GetReservationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpireReservationService implements ExpireReservationUseCase {

    private final GetReservationPort getReservationPort;
    private final ExpireReservationManager expireReservationManager;

	@Override
    public void expireReservation() {
        List<Reservation> reservations = getReservationPort.getPendingReservations();

        for (Reservation pendingReservation : reservations) {
            try {
                expireReservationManager.process(pendingReservation);
            } catch (Exception e) {
                log.warn("예약 만료 처리 스케줄러 처리 중 예외발생", e);
            }
        }
    }
}
