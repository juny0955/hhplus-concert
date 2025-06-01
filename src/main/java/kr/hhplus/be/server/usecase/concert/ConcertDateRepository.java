package kr.hhplus.be.server.usecase.concert;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.hhplus.be.server.interfaces.gateway.repository.concert.ConcertDateEntity;

public interface ConcertDateRepository extends JpaRepository<ConcertDateEntity, String> {

	@Query("""
		select c
		from ConcertDateEntity c
		where c.concert.id = :concertId
			and c.date > current_timestamp
			and c.deadline > current_timestamp
	""")
	List<ConcertDateEntity> findAvailableDates(String concertId);
}
