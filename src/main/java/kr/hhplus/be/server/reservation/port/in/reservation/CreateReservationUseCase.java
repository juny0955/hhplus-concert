package kr.hhplus.be.server.reservation.port.in.reservation;

import kr.hhplus.be.server.reservation.domain.Reservation;

public interface CreateReservationUseCase {
	Reservation createReservation(ReserveSeatCommand command) throws Exception;
}
