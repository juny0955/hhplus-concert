package kr.hhplus.be.server.reservation.port.out.reservation;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.reservation.domain.reservation.Reservation;

import java.util.List;
import java.util.UUID;

public interface GetReservationPort {
    Reservation getReservation(UUID reservationId) throws CustomException;
    List<Reservation> getPendingReservations();
}
