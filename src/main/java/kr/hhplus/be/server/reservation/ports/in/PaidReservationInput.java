package kr.hhplus.be.server.reservation.ports.in;

import java.util.UUID;

import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.reservation.domain.Reservation;

public interface PaidReservationInput {
	Reservation paidReservation(UUID reservationId) throws CustomException;
}
