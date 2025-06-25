package kr.hhplus.be.server.usecase.user;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.infrastructure.persistence.user.UserManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private static final String LOCK_KEY = "user:";

	private final UserManager userManager;
	private final DistributedLockManager distributedLockManager;

	public User getUser(UUID userId) throws CustomException {
		return userManager.getUser(userId);
	}

	public User chargePoint(UUID userId, BigDecimal point) throws Exception {
		String lockKey = LOCK_KEY + "charge:" + userId;

		// user:charge:{userId} 락 획득 후 포인트 충전 트랜잭션 수행
		return distributedLockManager.executeWithLockHasReturn(
			lockKey,
			() -> userManager.chargePoint(userId,point)
		);
	}
}
