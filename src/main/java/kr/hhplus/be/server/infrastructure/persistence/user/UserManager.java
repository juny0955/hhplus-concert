package kr.hhplus.be.server.infrastructure.persistence.user;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserManager {

	private final UserRepository userRepository;

	public User getUser(UUID userId) throws CustomException {
		return findUser(userId);
	}

	@Transactional
	public User chargePoint(UUID userId, BigDecimal point) throws CustomException {
		User user = findUser(userId);
		return userRepository.save(user.charge(point));
	}

	private User findUser(UUID userId) throws CustomException {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
