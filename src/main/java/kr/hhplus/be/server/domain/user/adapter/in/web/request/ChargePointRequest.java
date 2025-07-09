package kr.hhplus.be.server.domain.user.adapter.in.web.request;

import java.math.BigDecimal;

public record ChargePointRequest(
	BigDecimal point
) {
}
