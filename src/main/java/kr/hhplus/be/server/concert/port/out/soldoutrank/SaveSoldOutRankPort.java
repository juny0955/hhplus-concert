package kr.hhplus.be.server.concert.port.out.soldoutrank;

import kr.hhplus.be.server.concert.domain.soldoutrank.SoldOutRank;

public interface SaveSoldOutRankPort {
	SoldOutRank save(SoldOutRank soldOutRank);
}
