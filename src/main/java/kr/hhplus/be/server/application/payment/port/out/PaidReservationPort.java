package kr.hhplus.be.server.application.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.exception.CustomException;

public interface PaidReservationPort {
	Reservation paidReservation(UUID reservationId) throws CustomException;
}
