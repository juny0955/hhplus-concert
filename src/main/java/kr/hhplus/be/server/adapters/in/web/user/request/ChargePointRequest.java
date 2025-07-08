package kr.hhplus.be.server.adapters.in.web.user.request;

import java.math.BigDecimal;

public record ChargePointRequest(
	BigDecimal point
) {
}
