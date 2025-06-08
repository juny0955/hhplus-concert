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
		select cd
		from ConcertDateEntity cd
		where cd.id = :concertDateId
			and cd.concertId = :concertId
			and cd.deadline > current_timestamp
	""")
	Optional<ConcertDateEntity> findAvailableDate(String concertId, String concertDateId);
}
