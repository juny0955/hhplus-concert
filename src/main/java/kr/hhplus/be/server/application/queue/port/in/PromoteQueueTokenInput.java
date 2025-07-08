package kr.hhplus.be.server.application.queue.port.in;

import java.util.List;

import kr.hhplus.be.server.domain.concert.Concert;

public interface PromoteQueueTokenInput {
	void promoteQueueToken(List<Concert> concerts);
}
