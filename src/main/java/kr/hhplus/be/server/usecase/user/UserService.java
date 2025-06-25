package kr.hhplus.be.server.usecase.user;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

	private static final BigDecimal MIN_CHARGE_POINT = BigDecimal.valueOf(1000);

	private final UserRepository userRepository;

	public User getUser(UUID userId) throws CustomException {
		return findUser(userId, false);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public User chargePoint(UUID userId, BigDecimal point) throws CustomException {
		if (point.compareTo(MIN_CHARGE_POINT) < 0) {
			log.warn("유저 포인트 충전 실패 - 최소 충전 금액 미만: USER_ID - {}, CHARGE_POINT - {}", userId, point);
			throw new CustomException(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT);
		}

		User user = findUser(userId, true);
		User saved = userRepository.save(user.charge(point));

		log.info("유저 포인트 충전: USER_ID - {}, CHARGE_POINT - {}, AFTER_POINT - {}", userId, point, user.amount());
		return saved;
	}

	private User findUser(UUID userId, boolean withLock) throws CustomException {
		if (withLock)
			return userRepository.findByIdForUpdate(userId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		return userRepository.findById(userId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
