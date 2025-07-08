package kr.hhplus.be.server.application.concert.port.out;

import java.util.List;

import kr.hhplus.be.server.domain.concert.Concert;

public interface GetOpenConcertsPort {
	List<Concert> getOpenConcerts();
}
