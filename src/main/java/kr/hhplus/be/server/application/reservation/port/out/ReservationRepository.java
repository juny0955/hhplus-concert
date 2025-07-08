package kr.hhplus.be.server.application.reservation.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.exception.CustomException;

public interface ReservationRepository {
	Reservation save(Reservation reservation);
	Optional<Reservation> findById(UUID reservationId) throws CustomException;
	List<Reservation> findAll();

	void deleteAll();

	List<Reservation> findByStatusPending();
}
