package kr.hhplus.be.server.user.port.out;

import kr.hhplus.be.server.user.domain.PaidUserEvent;

public interface UserEventPublishPort {
	void publishPaidUserEvent(PaidUserEvent event);
}
