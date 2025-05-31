package kr.hhplus.be.server.usecase.user;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.entity.user.User;
import kr.hhplus.be.server.interfaces.gateway.repository.user.UserEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final static BigDecimal MIN_CHARGE_POINT = BigDecimal.valueOf(1000);

	private final UserRepository userRepository;

	public User getUser(UUID userId) {
		UserEntity userEntity = getUserEntity(userId);

		return userEntity.toDomain();
	}

	@Transactional
	public User chargePoint(UUID userId, BigDecimal point) {
		if (point.compareTo(MIN_CHARGE_POINT) < 0)
			throw new IllegalArgumentException("최소 충전 금액("+MIN_CHARGE_POINT+"원) 보다 적은 금액은 충전 할 수 없습니다.");

		UserEntity userEntity = getUserEntity(userId);

		userEntity.charge(point);
		return userEntity.toDomain();
	}

	private UserEntity getUserEntity(UUID userId) {
		return userRepository.findById(userId.toString())
			.orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));
	}
}
