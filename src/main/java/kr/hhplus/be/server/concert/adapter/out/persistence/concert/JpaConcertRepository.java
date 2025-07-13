package kr.hhplus.be.server.concert.adapter.out.persistence.concert;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaConcertRepository extends JpaRepository<ConcertEntity, String> {

	@Query(value = """
		select c
		from ConcertEntity c
		where c.openTime <= CURRENT_TIMESTAMP
			and c.soldOutTime is null
	""")
	List<ConcertEntity> findByOpenConcerts();

	@Query("""
		select c
		from ConcertEntity c
		join ConcertDateEntity cd on cd.concertId = c.id
		where cd.id = :concertDateId
	""")
	Optional<ConcertEntity> findByConcertDateId(String concertDateId);
}
