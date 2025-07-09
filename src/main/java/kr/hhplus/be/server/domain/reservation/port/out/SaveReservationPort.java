package kr.hhplus.be.server.domain.reservation.port.out;

import kr.hhplus.be.server.domain.reservation.domain.Reservation;

public interface SaveReservationPort {
    Reservation saveReservation(Reservation reservation);
}
