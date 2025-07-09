package kr.hhplus.be.server.domain.soldoutRank.port.out;

import java.util.UUID;

public interface RedisSoldOutRankRepository {
    Long updateRank(UUID concertId, long score);
}
