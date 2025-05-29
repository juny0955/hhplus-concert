package kr.hhplus.be.server.interfaces.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record UserPointResponse(
	UUID userId,
	BigDecimal amount
) {
}
