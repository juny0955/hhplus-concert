package kr.hhplus.be.server.reservation.port.out.reservation;

import kr.hhplus.be.server.reservation.domain.reservation.Reservation;

public interface SaveReservationPort {
    Reservation saveReservation(Reservation reservation);
}
