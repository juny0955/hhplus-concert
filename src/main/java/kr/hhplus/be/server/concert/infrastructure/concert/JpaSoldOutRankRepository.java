package kr.hhplus.be.server.concert.infrastructure.concert;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSoldOutRankRepository extends JpaRepository<SoldOutRankEntity, String> {
}
