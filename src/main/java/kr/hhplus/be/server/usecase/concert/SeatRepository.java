package kr.hhplus.be.server.usecase.concert;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.hhplus.be.server.entity.concert.SeatStatus;
import kr.hhplus.be.server.interfaces.gateway.repository.concert.SeatEntity;

public interface SeatRepository extends JpaRepository<SeatEntity, String> {

	@Query("""
		select s 
		from SeatEntity s 
		where s.concertDate.id = :concertDateId 
			and s.status = :status
	""")
	List<SeatEntity> findAvailableSeats(String concertDateId, SeatStatus status);
}
