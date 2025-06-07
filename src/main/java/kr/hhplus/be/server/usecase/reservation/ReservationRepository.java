package kr.hhplus.be.server.usecase.reservation;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.usecase.exception.CustomException;

public interface ReservationRepository {
	Reservation save(Reservation reservation);
	Optional<Reservation> findById(UUID reservationId) throws CustomException;
}
