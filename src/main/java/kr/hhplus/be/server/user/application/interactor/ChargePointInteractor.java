package kr.hhplus.be.server.user.application.interactor;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.common.framework.exception.ErrorCode;
import kr.hhplus.be.server.common.infrastructure.persistence.lock.DistributedLockManager;
import kr.hhplus.be.server.user.application.service.UserApplicationService;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.ports.in.ChargePointInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChargePointInteractor implements ChargePointInput {

	private static final String USER_LOCK_KEY = "user:";
	private static final BigDecimal MIN_CHARGE_POINT = BigDecimal.valueOf(1000);

	private final UserApplicationService userApplicationService;
	private final DistributedLockManager distributedLockManager;


	@Override
	public User chargePoint(UUID userId, BigDecimal point) throws Exception {
		validateMinChargePoint(userId, point);

		String lockKey = USER_LOCK_KEY + userId;
		User user = distributedLockManager.executeWithLockHasReturn(
			lockKey,
			() -> userApplicationService.chargePoint(userId, point)
		);

		log.info("유저 포인트 충전: USER_ID - {}, CHARGE_POINT - {}, AFTER_POINT - {}", userId, point, user.amount());
		return user;
	}

	private void validateMinChargePoint(UUID userId, BigDecimal point) throws CustomException {
		if (point.compareTo(MIN_CHARGE_POINT) < 0) {
			log.warn("유저 포인트 충전 실패 - 최소 충전 금액 미만: USER_ID - {}, CHARGE_POINT - {}", userId, point);
			throw new CustomException(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT);
		}
	}
}
