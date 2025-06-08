package kr.hhplus.be.server.domain.reservation;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.framework.exception.CustomException;

public interface ReservationRepository {
	Reservation save(Reservation reservation);
	Optional<Reservation> findById(UUID reservationId) throws CustomException;
}
