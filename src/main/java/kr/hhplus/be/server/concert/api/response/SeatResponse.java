package kr.hhplus.be.server.concert.api.response;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.SeatClass;
import kr.hhplus.be.server.concert.domain.seat.SeatStatus;
import lombok.Builder;

@Builder
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
	public static SeatResponse from(Seat seat) {
		return SeatResponse.builder()
			.seatId(seat.id())
			.seatNo(seat.seatNo())
			.price(seat.price())
			.seatClass(seat.seatClass())
			.status(seat.status())
			.build();
	}
}
