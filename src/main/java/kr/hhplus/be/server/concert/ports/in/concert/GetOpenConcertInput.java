package kr.hhplus.be.server.concert.ports.in.concert;

import java.util.List;

import kr.hhplus.be.server.concert.domain.concert.Concert;

public interface GetOpenConcertInput {
	List<Concert> getOpenConcert();
}
