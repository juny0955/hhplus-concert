package kr.hhplus.be.server.user.usecase;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.port.in.ChargePointUseCase;
import kr.hhplus.be.server.user.port.in.ExistsUserUseCase;
import kr.hhplus.be.server.user.port.in.FindUserUseCase;
import kr.hhplus.be.server.user.port.in.UsePointUseCase;
import kr.hhplus.be.server.user.port.out.GetUserPort;
import kr.hhplus.be.server.user.port.out.SaveUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserService implements
        ChargePointUseCase,
        FindUserUseCase,
        UsePointUseCase,
        ExistsUserUseCase {

    private final GetUserPort getUserPort;
    private final SaveUserPort saveUserPort;

    @Override
    @DistributedLock(key = "user:#userId")
    @Transactional
    public User chargePoint(UUID userId, BigDecimal point) throws Exception {
        User user = getUserPort.getUser(userId);
        User chargedUser = saveUserPort.saveUser(user.chargePoint(point));

        log.info("유저 포인트 충전: USER_ID - {}, CHARGE_POINT - {}, AFTER_POINT - {}", userId, point, chargedUser.amount());
        return chargedUser;
    }

    @Override
    public User find(UUID userId) throws CustomException {
        return getUserPort.getUser(userId);
    }

    @Override
    @DistributedLock(key = "user:#userId")
    @Transactional
    public User usePoint(UUID userId, BigDecimal point) throws Exception {
        User user = getUserPort.getUser(userId);
        User usedUser = saveUserPort.saveUser(user.usePoint(point));

        log.info("유저 포인트 사용: USER_ID - {}, USE_POINT - {}, AFTER_POINT - {}", userId, point, usedUser.amount());
        return usedUser;
    }

    @Override
    public void existsUser(UUID userId) throws CustomException {
        getUserPort.existsUser(userId);
    }
}
