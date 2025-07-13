package kr.hhplus.be.server.reservation.port.in.reservation;

import java.util.UUID;

import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.common.exception.CustomException;


public interface PaidReservationUseCase {
	Reservation paidReservation(UUID reservationId) throws CustomException;
}
