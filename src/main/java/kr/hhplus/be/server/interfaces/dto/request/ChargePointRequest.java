package kr.hhplus.be.server.interfaces.dto.request;

import java.math.BigDecimal;

public record ChargePointRequest(
	BigDecimal point
) {
}
