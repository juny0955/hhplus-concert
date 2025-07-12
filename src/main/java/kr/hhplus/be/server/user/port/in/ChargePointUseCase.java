package kr.hhplus.be.server.user.port.in;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.user.domain.User;

public interface ChargePointUseCase {
	User chargePoint(UUID userId, BigDecimal point) throws Exception;
}
