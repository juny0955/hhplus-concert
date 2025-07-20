package kr.hhplus.be.server.user.usecase;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.payment.domain.PaymentEvent;
import kr.hhplus.be.server.reservation.domain.PaidUserFailEvent;
import kr.hhplus.be.server.user.domain.PaidUserEvent;
import kr.hhplus.be.server.user.domain.PaymentFailEvent;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.port.in.ChargePointUseCase;
import kr.hhplus.be.server.user.port.in.ExistsUserUseCase;
import kr.hhplus.be.server.user.port.in.FindUserUseCase;
import kr.hhplus.be.server.user.port.in.UsePointUseCase;
import kr.hhplus.be.server.user.port.out.GetUserPort;
import kr.hhplus.be.server.user.port.out.SaveUserPort;
import kr.hhplus.be.server.user.port.out.UserEventPublishPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private final UserEventPublishPort userEventPublishPort;

    @Override
    @DistributedLock(key = "'user:'+#userId")
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

    @DistributedLock(key = "'user:'+#event.userId()")
    @Transactional
    @Override
    public void usePoint(PaymentEvent event) {
        try {
            User user = getUserPort.getUser(event.userId());
            User usedUser = saveUserPort.saveUser(user.usePoint(event.amount()));

            log.info("유저 포인트 사용: USER_ID - {}, USE_POINT - {}, AFTER_POINT - {}", event.userId(), event.amount(), usedUser.amount());
            userEventPublishPort.publishPaidUserEvent(PaidUserEvent.of(event));
        } catch (Exception e) {
            log.error("유저 포인트 사용 실패: {}", e.getMessage());
            userEventPublishPort.publishPaymentFailEvent(PaymentFailEvent.of(event, e.getMessage()));
        }
    }

    @DistributedLock(key = "'user:'+#event.userId()")
    @Transactional
    @Override
    public void failUsePoint(PaidUserFailEvent event) throws CustomException {
        User user = getUserPort.getUser(event.userId());
        saveUserPort.saveUser(user.usePointFail(event.amount()));

        userEventPublishPort.publishPaymentFailEvent(PaymentFailEvent.of(event));
    }

    @Override
    public void existsUser(UUID userId) throws CustomException {
        getUserPort.existsUser(userId);
    }
}
