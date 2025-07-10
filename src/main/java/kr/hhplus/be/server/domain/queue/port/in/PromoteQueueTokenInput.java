package kr.hhplus.be.server.domain.queue.port.in;

import java.util.List;

import kr.hhplus.be.server.domain.concert.domain.concert.Concert;

public interface PromoteQueueTokenInput {
	void promoteQueueToken(List<Concert> concerts);
}
