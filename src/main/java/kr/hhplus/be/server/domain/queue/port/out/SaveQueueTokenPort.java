package kr.hhplus.be.server.domain.queue.port.out;

import kr.hhplus.be.server.domain.queue.domain.QueueToken;

public interface SaveQueueTokenPort {
	void save(QueueToken queueToken);
}
