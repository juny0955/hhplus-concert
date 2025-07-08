package kr.hhplus.be.server.application.reservation.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.reservation.port.in.PaidReservationInput;
import kr.hhplus.be.server.application.reservation.service.ReservationService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaidReservationInteractor implements PaidReservationInput {

	private final ReservationService reservationService;

	@Override
	public Reservation paidReservation(UUID reservationId) throws CustomException {
		return reservationService.paidReservation(reservationId);
	}
}
