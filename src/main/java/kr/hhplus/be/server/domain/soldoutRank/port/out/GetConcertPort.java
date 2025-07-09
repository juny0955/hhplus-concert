package kr.hhplus.be.server.domain.soldoutRank.port.out;

import kr.hhplus.be.server.domain.concert.domain.Concert;

import java.util.UUID;

public interface GetConcertPort {
    Concert getConcertByConcertDateId(UUID concertDateId);
}
