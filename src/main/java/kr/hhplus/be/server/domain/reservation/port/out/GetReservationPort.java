package kr.hhplus.be.server.domain.reservation.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;

import java.util.List;
import java.util.UUID;

public interface GetReservationPort {
    Reservation getReservation(UUID reservationId) throws CustomException;
    List<Reservation> getPendingReservations();
}
