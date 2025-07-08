package kr.hhplus.be.server.adapters.out.persistence.reservation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.reservation.port.out.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationJpaGateway implements ReservationRepository {

	private final JpaReservationRepository jpaReservationRepository;

	@Override
	public Reservation save(Reservation reservation) {
		ReservationEntity reservationEntity = ReservationEntity.from(reservation);
		return jpaReservationRepository.save(reservationEntity).toDomain();
	}

	@Override
	public Optional<Reservation> findById(UUID reservationId) throws CustomException {
		return jpaReservationRepository.findById(reservationId.toString())
			.map(ReservationEntity::toDomain);
	}

	@Override
	public List<Reservation> findAll() {
		return jpaReservationRepository.findAll().stream()
			.map(ReservationEntity::toDomain)
			.toList();
	}

	@Override
	public void deleteAll() {
		jpaReservationRepository.deleteAll();
	}

	@Override
	public List<Reservation> findByStatusPending() {
		return jpaReservationRepository.findByStatusPending().stream()
				.map(ReservationEntity::toDomain)
				.toList();
	}
}
