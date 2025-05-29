package kr.hhplus.be.server.interfaces.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record ConcertDateResponse(
	@Schema(description = "콘서트 날짜 ID")
	UUID id,
	@Schema(description = "공연 일시")
	LocalDateTime date,
	@Schema(description = "예약 마감 일시")
	LocalDateTime deadline,
	@Schema(description = "잔여 좌석 수")
	Integer remainingSeatCount
) {
}
