package kr.hhplus.be.server.domain.concert.port.in.concert;

import java.util.List;

import kr.hhplus.be.server.domain.concert.domain.concert.Concert;

public interface GetOpenConcertUseCase {
	List<Concert> getOpenConcert();
}
