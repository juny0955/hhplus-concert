package kr.hhplus.be.server.domain.payment.adapter.out.internal.reservation;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.payment.port.out.ReservationQueryPort;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.domain.reservation.port.in.PaidReservationUseCase;
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
