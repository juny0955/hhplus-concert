package kr.hhplus.be.server.domain.payment.adapter.out.reservation;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.port.out.PaidReservationPort;
import kr.hhplus.be.server.domain.reservation.port.in.PaidReservationUseCase;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.common.exception.CustomException;
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
