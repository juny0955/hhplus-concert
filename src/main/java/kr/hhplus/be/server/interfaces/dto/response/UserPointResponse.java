package kr.hhplus.be.server.interfaces.dto.response;

import java.math.BigDecimal;

public record UserPointResponse(
	String userId,
	BigDecimal amount
) {
}
