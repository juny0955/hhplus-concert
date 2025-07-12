package kr.hhplus.be.server.concert.port.out.concert;

import kr.hhplus.be.server.concert.domain.concert.Concert;

public interface SaveConcertPort {
    Concert saveConcert(Concert concert);
}
