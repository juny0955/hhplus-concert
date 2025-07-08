package kr.hhplus.be.server.reservation.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.reservation.domain.Reservation;

public interface ReservationRepository {
	Reservation save(Reservation reservation);
	Optional<Reservation> findById(UUID reservationId) throws CustomException;
	List<Reservation> findAll();

	void deleteAll();

	List<Reservation> findByStatusPending();
}
