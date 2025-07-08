package kr.hhplus.be.server.application.soldoutRank.port.out;

import java.util.UUID;

public interface RedisSoldOutRankRepository {
    Long updateRank(UUID concertId, long score);
}
