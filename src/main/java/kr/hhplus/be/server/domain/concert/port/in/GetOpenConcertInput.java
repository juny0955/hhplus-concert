package kr.hhplus.be.server.domain.concert.port.in;

import java.util.List;

import kr.hhplus.be.server.domain.concert.domain.Concert;

public interface GetOpenConcertInput {
	List<Concert> getOpenConcert();
}
