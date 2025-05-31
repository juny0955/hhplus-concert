package kr.hhplus.be.server.interfaces.api.concert.concert.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.entity.concert.SeatClass;
import kr.hhplus.be.server.entity.concert.SeatStatus;

public record SeatResponse(
	@Schema(description = "좌석 ID")
	UUID seatId,
	@Schema(description = "좌석 번호")
	Integer seatNo,
	@Schema(description = "좌석 가격")
	BigDecimal price,
	@Schema(description = "좌석 등급")
	SeatClass seatClass,
	@Schema(description = "좌석 상태")
	SeatStatus status
) {
}
