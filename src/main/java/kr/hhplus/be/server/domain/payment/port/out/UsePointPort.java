package kr.hhplus.be.server.domain.payment.port.out;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.domain.user.domain.User;

public interface UsePointPort {
	User usePoint(UUID userId, BigDecimal point) throws Exception;
}
