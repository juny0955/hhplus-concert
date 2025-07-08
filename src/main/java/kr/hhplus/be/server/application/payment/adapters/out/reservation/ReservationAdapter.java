package kr.hhplus.be.server.application.payment.adapters.out.reservation;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.payment.port.out.PaidReservationPort;
import kr.hhplus.be.server.application.reservation.port.in.PaidReservationUseCase;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationAdapter implements PaidReservationPort {

	private final PaidReservationUseCase paidReservationUseCase;

	@Override
	public Reservation paidReservation(UUID reservationId) throws CustomException {
		return paidReservationUseCase.paidReservation(reservationId);
	}
}
