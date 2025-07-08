package kr.hhplus.be.server.application.soldoutRank.port.out;

import kr.hhplus.be.server.domain.soldoutRank.SoldOutRank;

public interface SoldOutRankRepository {
	SoldOutRank save(SoldOutRank soldOutRank);
}
