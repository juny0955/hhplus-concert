package kr.hhplus.be.server.usecase.concert;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.hhplus.be.server.interfaces.gateway.repository.concert.ConcertDateEntity;

public interface ConcertDateRepository extends JpaRepository<ConcertDateEntity, String> {

	@Query("""
		select cd
		from ConcertDateEntity cd
		where cd.concert.id = :concertId
			and cd.deadline > current_timestamp
	""")
	List<ConcertDateEntity> findAvailableDates(String concertId);

	@Query("""
		select cd
		from ConcertDateEntity cd
		where cd.id = :concertDateId
			and cd.concert.id = :concertId
			and cd.deadline > current_timestamp
	""")
	Optional<ConcertDateEntity> findAvailableDate(String concertId, String concertDateId);
}
