package kr.hhplus.be.server.usecase.concert;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.interfaces.gateway.repository.concert.ConcertEntity;

public interface ConcertRepository extends JpaRepository<ConcertEntity, String> {
}
