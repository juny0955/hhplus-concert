package kr.hhplus.be.server.domain.soldoutRank.adapter.out.persistence;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.soldoutRank.port.out.SaveSoldOutRankPort;
import kr.hhplus.be.server.domain.soldoutRank.domain.SoldOutRank;
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
