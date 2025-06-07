package kr.hhplus.be.server.interfaces.gateway.repository.reservation;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.reservation.ReservationRepository;
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
		return jpaReservationRepository.findById(reservationId)
			.map(ReservationEntity::toDomain);
	}
}
