package kr.hhplus.be.server.infrastructure.persistence.seat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

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
		update SeatEntity
			set status = 'RESERVED'
		where id = :seatId
			and status = 'AVAILABLE'
	""")
	@Modifying
	int updateStatusReserved(String seatId);

	@Query("""
		select s
		from SeatEntity s
		where s.id = :seatId
			and s.concertDateId = :concertDateId
	""")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@QueryHints({
		@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")
	})
	Optional<SeatEntity> findBySeatIdAndConcertDateIdForUpdate(String seatId, String concertDateId);
}
