package kr.hhplus.be.server.concert.port.out.soldoutrank;

import java.util.UUID;

public interface RedisSoldOutRankPort {
    Long updateRank(UUID concertId, long score);
}
