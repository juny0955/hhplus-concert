package kr.hhplus.be.server.application.concert.port.in;

import java.util.List;

import kr.hhplus.be.server.domain.concert.Concert;

public interface GetOpenConcertInput {
	List<Concert> getOpenConcert();
}
