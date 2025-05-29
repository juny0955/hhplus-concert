package kr.hhplus.be.server.interfaces.api.user.dto.request;

import java.math.BigDecimal;

public record ChargePointRequest(
	BigDecimal point
) {
}
