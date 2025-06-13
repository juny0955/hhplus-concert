package kr.hhplus.be.server.infrastructure.persistence.concertDate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaConcertDateRepository extends JpaRepository<ConcertDateEntity, String> {

	@Query("""
		select cd
		from ConcertDateEntity cd
		where cd.concertId = :concertId
			and cd.deadline > current_timestamp
	""")
	List<ConcertDateEntity> findAvailableDates(String concertId);

	@Query("""
		select
			cd.id,
			cd.concertId,
			cd.date,
			cd.deadline,
			cd.createdAt,
			cd.updatedAt,
			(
				select count(s.id)
				from SeatEntity s
				where s.concertDateId = cd.id
					and s.status = 'AVAILABLE'
			) as remainingSeatCount
		from ConcertDateEntity cd
		where cd.concertId = 'xxx'
			and cd.deadline > CURRENT_TIMESTAMP
			and exists (
				select 1
				from SeatEntity s
				where s.concertDateId = cd.id
					and s.status = 'AVAILABLE'
			)
	""")
	List<Object[]> findAvailableDatesWithAvailableSeatCount(String concertId);

	@Query("""
		select cd
		from ConcertDateEntity cd
		where cd.id = :concertDateId
			and cd.concertId = :concertId
			and cd.deadline > current_timestamp
	""")
	Optional<ConcertDateEntity> findAvailableDate(String concertId, String concertDateId);
}
