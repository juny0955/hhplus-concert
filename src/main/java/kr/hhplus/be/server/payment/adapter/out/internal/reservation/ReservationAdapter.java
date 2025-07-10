package kr.hhplus.be.server.payment.adapter.out.internal.reservation;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.payment.port.out.ReservationQueryPort;
import kr.hhplus.be.server.reservation.domain.reservation.Reservation;
import kr.hhplus.be.server.reservation.port.in.PaidReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationAdapter implements ReservationQueryPort {

	private final PaidReservationUseCase paidReservationUseCase;

	@Override
	public Reservation paidReservation(UUID reservationId) throws CustomException {
		return paidReservationUseCase.paidReservation(reservationId);
	}
}
