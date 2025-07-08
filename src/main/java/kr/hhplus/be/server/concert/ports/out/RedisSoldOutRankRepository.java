package kr.hhplus.be.server.concert.ports.out;

import java.util.UUID;

public interface RedisSoldOutRankRepository {
    Long updateRank(UUID concertId, long score);
}
