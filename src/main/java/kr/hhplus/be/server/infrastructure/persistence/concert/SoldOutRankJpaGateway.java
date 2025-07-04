package kr.hhplus.be.server.infrastructure.persistence.concert;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.concert.SoldOutRank;
import kr.hhplus.be.server.domain.concert.SoldOutRankRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SoldOutRankJpaGateway implements SoldOutRankRepository {

	private final JpaSoldOutRankRepository jpaSoldOutRankRepository;

	@Override
	public SoldOutRank save(SoldOutRank soldOutRank) {
		return jpaSoldOutRankRepository.save(SoldOutRankEntity.from(soldOutRank))
			.toDomain();
	}
}
