package kr.hhplus.be.server.domain.soldoutRank.port.out;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.Concert;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SoldOutConcertPort {
    Concert soldOutConcert(UUID concertId, LocalDateTime soldOutTime) throws CustomException;
}
