package kr.hhplus.be.server.domain.soldoutRank.port.out;

import kr.hhplus.be.server.domain.soldoutRank.domain.SoldOutRank;

public interface SoldOutRankRepository {
	SoldOutRank save(SoldOutRank soldOutRank);
}
