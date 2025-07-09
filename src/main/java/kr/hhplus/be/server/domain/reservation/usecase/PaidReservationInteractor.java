package kr.hhplus.be.server.domain.reservation.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.reservation.port.in.PaidReservationUseCase;
import kr.hhplus.be.server.domain.reservation.service.ReservationService;
import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaidReservationInteractor implements PaidReservationUseCase {

	private final ReservationService reservationService;

	@Override
	@DistributedLock(key = "reservation:#reservationId")
	public Reservation paidReservation(UUID reservationId) throws CustomException {
		return reservationService.paidReservation(reservationId);
	}
}
