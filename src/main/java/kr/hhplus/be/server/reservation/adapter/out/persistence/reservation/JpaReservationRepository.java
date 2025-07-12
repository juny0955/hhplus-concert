package kr.hhplus.be.server.reservation.adapter.out.persistence.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaReservationRepository extends JpaRepository<ReservationEntity, String> {

    @Query("""
        select r
        from ReservationEntity r
        where r.status = 'PENDING'
    """)
    List<ReservationEntity> findByStatusPending();
}
