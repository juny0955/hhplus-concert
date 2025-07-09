package kr.hhplus.be.server.domain.concert.port.in;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.concert.domain.Concert;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SoldOutConcertUseCase {
    Concert soldOutConcert(UUID concertId, LocalDateTime soldOutTime) throws CustomException;
}
