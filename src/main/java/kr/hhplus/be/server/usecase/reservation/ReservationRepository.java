package kr.hhplus.be.server.usecase.reservation;

import kr.hhplus.be.server.domain.reservation.Reservation;

public interface ReservationRepository {
	Reservation save(Reservation reservation);
}
