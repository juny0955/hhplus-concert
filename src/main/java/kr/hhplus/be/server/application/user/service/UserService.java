package kr.hhplus.be.server.application.user.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.user.port.out.UserRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;

	public User getUser(UUID userId) throws CustomException {
		return userRepository.findById(userId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	@Transactional
	public User chargePoint(UUID userId, BigDecimal point) throws CustomException {
		User user = getUser(userId);
		return userRepository.save(user.chargePoint(point));
	}

	@Transactional
	public User usePoint(UUID userId, BigDecimal point) throws CustomException {
		User user = getUser(userId);
		return userRepository.save(user.usePoint(point));
	}

	public void existsUser(UUID userId) throws CustomException {
		if (!userRepository.existsById(userId))
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
	}
}
