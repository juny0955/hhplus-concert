package kr.hhplus.be.server.interfaces.gateway.repository.reservation;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.usecase.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationJpaGateway implements ReservationRepository {

	private final JpaReservationRepository jpaReservationRepository;

	@Override
	public Reservation save(Reservation reservation) {
		ReservationEntity reservationEntity = ReservationEntity.from(reservation);
		ReservationEntity save = jpaReservationRepository.save(reservationEntity);
		return Reservation.from(save);
	}
}
