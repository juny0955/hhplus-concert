package kr.hhplus.be.server.interfaces.gateway.repository.reservation;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReservationRepository extends JpaRepository<ReservationEntity, UUID> {
}
