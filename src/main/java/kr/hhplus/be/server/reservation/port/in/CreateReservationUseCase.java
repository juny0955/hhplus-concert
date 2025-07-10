package kr.hhplus.be.server.reservation.port.in;

import kr.hhplus.be.server.reservation.domain.reservation.Reservation;

public interface CreateReservationUseCase {
	Reservation createReservation(ReserveSeatCommand command) throws Exception;
}
