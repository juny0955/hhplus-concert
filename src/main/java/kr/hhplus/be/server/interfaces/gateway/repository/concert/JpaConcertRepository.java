package kr.hhplus.be.server.interfaces.gateway.repository.concert;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaConcertRepository extends JpaRepository<ConcertEntity, String> {
}
