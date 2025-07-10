package kr.hhplus.be.server.domain.payment.port.out;

import kr.hhplus.be.server.domain.user.domain.User;

import java.math.BigDecimal;
import java.util.UUID;

public interface UserQueryPort {
    User usePoint(UUID userId, BigDecimal point) throws Exception;
}
