package kr.hhplus.be.server.queue.ports.in;

import java.util.List;

import kr.hhplus.be.server.concert.domain.concert.Concert;

public interface PromoteQueueTokenInput {
	void promoteQueueToken(List<Concert> concerts);
}
