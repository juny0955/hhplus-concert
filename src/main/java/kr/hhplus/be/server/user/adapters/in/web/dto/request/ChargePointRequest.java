package kr.hhplus.be.server.user.adapters.in.web.dto.request;

import java.math.BigDecimal;

public record ChargePointRequest(
	BigDecimal point
) {
}
