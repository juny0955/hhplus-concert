package kr.hhplus.be.server.infrastructure.persistence.seat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaSeatRepository extends JpaRepository<SeatEntity, String> {

	@Query("""
		select s
		from SeatEntity s
		     	inner join ConcertDateEntity cd ON cd.id = s.concertDateId
		where cd.concertId = :concertId
			and cd.id = :concertDateId
			and cd.deadline > CURRENT_TIMESTAMP
			and s.status = 'AVAILABLE'
		order by s.seatNo
	""")
	List<SeatEntity> findAvailableSeats(String concertId, String concertDateId);

	@Query("""
		select s
		from SeatEntity s
		where s.id = :seatId
			and s.concertDateId = :concertDateId
	""")
	Optional<SeatEntity> findBySeatIdAndConcertDateId(String seatId, String concertDateId);

	@Query("""
		select s
		from SeatEntity s
		where s.concertDateId = :concertDateId
	""")
	List<SeatEntity> findByConcertDateId(String concertDateId);
}
