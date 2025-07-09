package kr.hhplus.be.server.domain.concert.port.out;

import java.util.List;

import kr.hhplus.be.server.domain.concert.domain.Concert;

public interface GetOpenConcertsPort {
	List<Concert> getOpenConcerts();
}
