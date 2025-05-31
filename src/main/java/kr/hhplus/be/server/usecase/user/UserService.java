package kr.hhplus.be.server.usecase.user;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.entity.user.User;
import kr.hhplus.be.server.interfaces.gateway.repository.user.UserEntity;
import kr.hhplus.be.server.usecase.exception.CustomException;
import kr.hhplus.be.server.usecase.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

	private final static BigDecimal MIN_CHARGE_POINT = BigDecimal.valueOf(1000);

	private final UserRepository userRepository;

	public User getUser(UUID userId) throws CustomException {
		UserEntity userEntity = getUserEntity(userId);

		return userEntity.toDomain();
	}

	@Transactional
	public User chargePoint(UUID userId, BigDecimal point) throws CustomException {
		if (point.compareTo(MIN_CHARGE_POINT) < 0) {
			log.warn("유저 포인트 충전 실패 - 최소 충전 금액 미만: USER_ID - {}, CHARGE_POINT - {}", userId, point);
			throw new CustomException(ErrorCode.NOT_ENOUGH_MIN_CHARGE_POINT);
		}

		UserEntity userEntity = getUserEntity(userId);

		userEntity.charge(point);

		log.info("유저 포인트 충전: USER_ID - {}, CHARGE_POINT - {}, AFTER_POINT - {}", userId, point, userEntity.getAmount());
		return userEntity.toDomain();
	}

	private UserEntity getUserEntity(UUID userId) throws CustomException {
		try {
			UserEntity userEntity = userRepository.findById(userId.toString())
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

			log.debug("유저 조회: USER_ID - {}", userId);
			return userEntity;
		} catch (CustomException e) {
			log.warn("유저 조회 실패: USER_ID - {}", userId);
			throw e;
		}
	}
}
