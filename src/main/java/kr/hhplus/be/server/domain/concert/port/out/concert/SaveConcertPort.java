package kr.hhplus.be.server.domain.concert.port.out.concert;

import kr.hhplus.be.server.domain.concert.domain.concert.Concert;

public interface SaveConcertPort {
    Concert saveConcert(Concert concert);
}
