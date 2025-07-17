package kr.hhplus.be.server.payment.adapter.out.internal.reservation;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.payment.port.out.ReservationQueryPort;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.port.in.reservation.PaidReservationUseCase;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class PaymentReservationAdapter implements ReservationQueryPort {

	private final PaidReservationUseCase paidReservationUseCase;

	@Override
	public Reservation paidReservation(UUID reservationId) throws CustomException {
		return paidReservationUseCase.paidReservation(reservationId);
	}
}
