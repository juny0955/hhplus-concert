package kr.hhplus.be.server.concert.adapter.out.persistence.soldoutrank;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.concert.port.out.soldoutrank.SaveSoldOutRankPort;
import kr.hhplus.be.server.concert.domain.soldoutrank.SoldOutRank;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaveSoldOutRankJpaGateway implements SaveSoldOutRankPort {

	private final JpaSoldOutRankRepository jpaSoldOutRankRepository;

	@Override
	public SoldOutRank save(SoldOutRank soldOutRank) {
		return jpaSoldOutRankRepository.save(SoldOutRankEntity.from(soldOutRank))
			.toDomain();
	}
}
