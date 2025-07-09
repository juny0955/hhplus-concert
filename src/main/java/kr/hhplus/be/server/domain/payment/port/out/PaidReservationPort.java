package kr.hhplus.be.server.domain.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.common.exception.CustomException;

public interface PaidReservationPort {
	Reservation paidReservation(UUID reservationId) throws CustomException;
}
