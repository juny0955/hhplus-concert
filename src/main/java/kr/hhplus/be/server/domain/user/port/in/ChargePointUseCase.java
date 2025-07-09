package kr.hhplus.be.server.domain.user.port.in;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.domain.user.domain.User;

public interface ChargePointUseCase {
	User chargePoint(UUID userId, BigDecimal point) throws Exception;
}
