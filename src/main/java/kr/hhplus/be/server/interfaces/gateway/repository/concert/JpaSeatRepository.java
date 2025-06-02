package kr.hhplus.be.server.interfaces.gateway.repository.concert;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaSeatRepository extends JpaRepository<SeatEntity, String> {

	@Query("""
		select s 
		from SeatEntity s 
		where s.concertDate.id = :concertDateId 
			and s.status = "AVAILABLE"
	""")
	List<SeatEntity> findAvailableSeats(String concertDateId);

	@Query("""
		select count(s)
		from SeatEntity s
		where s.concertDate.id = :concertDateId
			and s.status = "AVAILABLE"
	""")
	Integer countRemainingSeat(String concertDateId);
}
