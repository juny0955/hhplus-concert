package kr.hhplus.be.server.concert.port.in.concert;

import java.util.List;

import kr.hhplus.be.server.concert.domain.concert.Concert;

public interface GetOpenConcertUseCase {
	List<Concert> getOpenConcert();
}
