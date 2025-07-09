package kr.hhplus.be.server.domain.reservation.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.common.exception.CustomException;

public interface ReservationRepository {
	Reservation save(Reservation reservation);
	Optional<Reservation> findById(UUID reservationId) throws CustomException;
	List<Reservation> findAll();

	void deleteAll();

	List<Reservation> findByStatusPending();
}
