package kr.hhplus.be.server.concert.port.in.concert;

import kr.hhplus.be.server.common.exception.CustomException;

import java.util.UUID;

public interface ExistsConcertUseCase {
    void existsConcert(UUID concertId) throws CustomException;
}
