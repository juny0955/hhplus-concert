package kr.hhplus.be.server.interfaces.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record SeatResponse(
	@Schema(description = "좌석 ID")
	UUID seatId,
	@Schema(description = "좌석 번호")
	Integer seatNo,
	@Schema(description = "좌석 가격")
	BigDecimal price,
	@Schema(description = "좌석 등급")
	String seatClass,
	@Schema(description = "좌석 상태")
	String status
) {
}
