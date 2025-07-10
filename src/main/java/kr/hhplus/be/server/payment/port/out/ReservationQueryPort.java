package kr.hhplus.be.server.payment.port.out;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.reservation.domain.Reservation;

public interface ReservationQueryPort {
    Reservation paidReservation(UUID reservationId) throws CustomException;
}
