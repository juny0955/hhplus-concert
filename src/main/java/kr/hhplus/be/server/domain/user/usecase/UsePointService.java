package kr.hhplus.be.server.domain.user.usecase;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.user.port.in.UsePointUseCase;
import kr.hhplus.be.server.domain.user.port.out.GetUserPort;
import kr.hhplus.be.server.domain.user.port.out.SaveUserPort;
import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsePointService implements UsePointUseCase {

	private final GetUserPort getUserPort;
	private final SaveUserPort saveUserPort;

	@Override
	@DistributedLock(key = "user:#userId")
	@Transactional
	public User usePoint(UUID userId, BigDecimal point) throws Exception {
		User user = getUserPort.getUser(userId);
		User usedUser = saveUserPort.saveUser(user.usePoint(point));

		log.info("유저 포인트 사용: USER_ID - {}, USE_POINT - {}, AFTER_POINT - {}", userId, point, usedUser.amount());
		return usedUser;
	}
}
