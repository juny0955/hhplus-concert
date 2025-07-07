package kr.hhplus.be.server.user.application.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.framework.exception.ErrorCode;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.user.ports.out.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserApplicationService {

	private final UserRepository userRepository;

	public User getUser(UUID userId) throws CustomException {
		return findUser(userId);
	}

	@Transactional
	public User chargePoint(UUID userId, BigDecimal point) throws CustomException {
		User user = findUser(userId);
		return userRepository.save(user.chargePoint(point));
	}

	@Transactional
	public User usePoint(UUID userId, BigDecimal point) throws CustomException {
		User user = findUser(userId);
		return userRepository.save(user.usePoint(point));
	}

	private User findUser(UUID userId) throws CustomException {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
