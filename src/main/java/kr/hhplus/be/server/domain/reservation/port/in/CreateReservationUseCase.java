package kr.hhplus.be.server.domain.reservation.port.in;

import kr.hhplus.be.server.domain.reservation.domain.Reservation;

public interface CreateReservationUseCase {
	Reservation createReservation(ReserveSeatCommand command) throws Exception;
}
