package kr.hhplus.be.server.reservation.application.interactor;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.reservation.application.service.ReservationApplicationService;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.ports.in.PaidReservationInput;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaidReservationInteractor implements PaidReservationInput {

	private final ReservationApplicationService reservationApplicationService;

	@Override
	public Reservation paidReservation(UUID reservationId) throws CustomException {
		return reservationApplicationService.paidReservation(reservationId);
	}
}
