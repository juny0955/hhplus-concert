package kr.hhplus.be.server.user.usecase;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.infrastructure.persistence.user.UserManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private static final BigDecimal MIN_CHARGE_POINT = BigDecimal.valueOf(1000);
	private static final String USER_LOCK_KEY = "user:";

	private final UserManager userManager;
	private final DistributedLockManager distributedLockManager;

	public User getUser(UUID userId) throws CustomException {
		return userManager.getUser(userId);
	}

	public User chargePoint(UUID userId, BigDecimal point) throws Exception {
		if (point.compareTo(MIN_CHARGE_POINT) < 0) {
			log.warn("유저 포인트 충전 실패 - 최소 충전 금액 미만: USER_ID - {}, CHARGE_POINT - {}", userId, point);
			throw new CustomException(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT);
		}

		String lockKey = USER_LOCK_KEY + userId;
		User user = distributedLockManager.executeWithLockHasReturn(
			lockKey,
			() -> userManager.chargePoint(userId, point)
		);
		log.info("유저 포인트 충전: USER_ID - {}, CHARGE_POINT - {}, AFTER_POINT - {}", userId, point, user.amount());
		return user;
	}
}
