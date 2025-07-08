package kr.hhplus.be.server.application.reservation.port.out;

import java.util.UUID;

import kr.hhplus.be.server.exception.CustomException;

public interface ValidOpenConcertPort {
	void validOpenConcert(UUID concertId) throws CustomException;
}
