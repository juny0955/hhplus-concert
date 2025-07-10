package kr.hhplus.be.server.domain.concert.port.out.soldoutrank;

import kr.hhplus.be.server.domain.concert.domain.soldoutrank.SoldOutRank;

public interface SaveSoldOutRankPort {
	SoldOutRank save(SoldOutRank soldOutRank);
}
