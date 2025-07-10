package kr.hhplus.be.server.domain.payment.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;

import java.util.UUID;

public interface ReservationQueryPort {
    Reservation paidReservation(UUID reservationId) throws CustomException;
}
