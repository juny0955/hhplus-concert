package kr.hhplus.be.server.concert.ports.out;

import kr.hhplus.be.server.concert.domain.concert.SoldOutRank;

public interface SoldOutRankRepository {
	SoldOutRank save(SoldOutRank soldOutRank);
}
