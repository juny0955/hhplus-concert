package kr.hhplus.be.server.infrastructure.persistence.reservation;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReservationRepository extends JpaRepository<ReservationEntity, UUID> {
}
