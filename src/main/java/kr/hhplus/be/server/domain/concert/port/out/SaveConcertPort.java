package kr.hhplus.be.server.domain.concert.port.out;

import kr.hhplus.be.server.domain.concert.domain.Concert;

public interface SaveConcertPort {

    Concert saveConcert(Concert concert);
}
