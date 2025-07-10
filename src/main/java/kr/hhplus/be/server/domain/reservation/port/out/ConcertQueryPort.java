package kr.hhplus.be.server.domain.reservation.port.out;

import kr.hhplus.be.server.common.exception.CustomException;

import java.util.UUID;

public interface ConcertQueryPort {
    void validDeadLine(UUID concertDateId) throws CustomException;
    void validOpenConcert(UUID concertId) throws CustomException;
}
