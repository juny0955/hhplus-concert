package kr.hhplus.be.server.domain.reservation.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.reservation.port.out.GetReservationPort;
import kr.hhplus.be.server.domain.reservation.port.out.SaveReservationPort;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.reservation.port.out.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.domain.Reservation;
import kr.hhplus.be.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationJpaGateway implements ReservationRepository, SaveReservationPort, GetReservationPort {

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
