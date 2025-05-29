package kr.hhplus.be.server.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record QueueTokenResponse(
	@Schema(description = "발금된 대기열 토큰")
	String queueToken,
	@Schema(description = "현재 대기 순서")
	Integer waitingNumber,
	@Schema(description = "예상 대기 시간")
	Integer waitTime
) {
}
