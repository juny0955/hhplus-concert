package kr.hhplus.be.server.adapters.out.persistence.concertRank;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.soldoutRank.port.out.SoldOutRankRepository;
import kr.hhplus.be.server.domain.soldoutRank.SoldOutRank;
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
