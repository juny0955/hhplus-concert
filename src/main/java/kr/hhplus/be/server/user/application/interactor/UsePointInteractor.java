package kr.hhplus.be.server.user.application.interactor;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.user.application.service.UserApplicationService;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.ports.in.UsePointInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsePointInteractor implements UsePointInput {

	private static final String USER_LOCK_KEY = "user:";

	private final UserApplicationService userApplicationService;
	private final DistributedLockManager distributedLockManager;

	@Override
	public User usePoint(UUID userId, BigDecimal point) throws Exception {
		String lockKey = USER_LOCK_KEY + userId;

		User user = distributedLockManager.executeWithLockHasReturn(
			lockKey,
			() -> userApplicationService.usePoint(userId, point)
		);

		log.info("유저 포인트 사용: USER_ID - {}, USE_POINT - {}, AFTER_POINT - {}", userId, point, user.amount());
		return user;
	}
}
