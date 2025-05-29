package kr.hhplus.be.server.interfaces.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserPointResponse(
	@Schema(description = "유저 ID")
	UUID userId,
	@Schema(description = "포인트 잔액")
	BigDecimal amount
) {
}
