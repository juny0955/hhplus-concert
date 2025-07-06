package kr.hhplus.be.server.concert.infrastructure.concert;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.domain.concert.SoldOutRank;
import kr.hhplus.be.server.concert.domain.concert.SoldOutRankRepository;
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
