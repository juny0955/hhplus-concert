package kr.hhplus.be.server.user.ports.in;

import java.math.BigDecimal;
import java.util.UUID;

import kr.hhplus.be.server.user.domain.User;

public interface ChargePointInput {
	User chargePoint(UUID userId, BigDecimal point) throws Exception;
}
