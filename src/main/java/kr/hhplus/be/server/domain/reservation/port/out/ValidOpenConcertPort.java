package kr.hhplus.be.server.domain.reservation.port.out;

import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;

public interface ValidOpenConcertPort {
	void validOpenConcert(UUID concertId) throws CustomException;
}
