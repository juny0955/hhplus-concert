package kr.hhplus.be.server.domain.soldoutRank.adapter.out.persistence;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.soldoutRank.port.out.SoldOutRankRepository;
import kr.hhplus.be.server.domain.soldoutRank.domain.SoldOutRank;
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
