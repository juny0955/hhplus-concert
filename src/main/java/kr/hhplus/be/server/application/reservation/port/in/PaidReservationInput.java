package kr.hhplus.be.server.application.reservation.port.in;

import java.util.UUID;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.exception.CustomException;


public interface PaidReservationInput {
	Reservation paidReservation(UUID reservationId) throws CustomException;
}
