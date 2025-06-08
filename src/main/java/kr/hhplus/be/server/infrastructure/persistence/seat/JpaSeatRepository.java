package kr.hhplus.be.server.infrastructure.persistence.seat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaSeatRepository extends JpaRepository<SeatEntity, String> {

	@Query("""
		select s 
		from SeatEntity s 
		where s.concertDateId = :concertDateId 
			and s.status = "AVAILABLE"
	""")
	List<SeatEntity> findAvailableSeats(String concertDateId);

	@Query("""
		select count(s)
		from SeatEntity s
		where s.concertDateId = :concertDateId
			and s.status = "AVAILABLE"
	""")
	Integer countRemainingSeat(String concertDateId);

	@Query("""
		select s
		from SeatEntity s
		where s.id = :seatId
			and s.concertDateId = :concertDateId
	""")
	Optional<SeatEntity> findBySeatIdAndConcertDateId(String seatId, String concertDateId);
}
