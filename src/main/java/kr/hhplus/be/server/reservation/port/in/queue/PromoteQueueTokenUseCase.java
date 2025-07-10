package kr.hhplus.be.server.reservation.port.in.queue;

import java.util.List;

import kr.hhplus.be.server.concert.domain.concert.Concert;

public interface PromoteQueueTokenUseCase {
	void promoteQueueToken(List<Concert> concerts);
}
