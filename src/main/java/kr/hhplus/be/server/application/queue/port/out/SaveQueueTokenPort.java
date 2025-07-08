package kr.hhplus.be.server.application.queue.port.out;

import kr.hhplus.be.server.domain.queue.QueueToken;

public interface SaveQueueTokenPort {
	void save(QueueToken queueToken);
}
