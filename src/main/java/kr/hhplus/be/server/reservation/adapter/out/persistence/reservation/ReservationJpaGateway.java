package kr.hhplus.be.server.reservation.adapter.out.persistence.reservation;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.port.out.reservation.GetReservationPort;
import kr.hhplus.be.server.reservation.port.out.reservation.SaveReservationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationJpaGateway implements SaveReservationPort, GetReservationPort {

	private final JpaReservationRepository jpaReservationRepository;

	@Override
	public Reservation saveReservation(Reservation reservation) {
		ReservationEntity reservationEntity = ReservationEntity.from(reservation);
		return jpaReservationRepository.save(reservationEntity).toDomain();
	}

	@Override
	public Reservation getReservation(UUID reservationId) throws CustomException {
		return jpaReservationRepository.findById(reservationId.toString())
				.map(ReservationEntity::toDomain)
				.orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
	}

	@Override
	public List<Reservation> getPendingReservations() {
		return jpaReservationRepository.findByStatusPending().stream()
				.map(ReservationEntity::toDomain)
				.toList();
	}
}
