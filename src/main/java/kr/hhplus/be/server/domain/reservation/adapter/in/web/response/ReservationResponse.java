package kr.hhplus.be.server.domain.reservation.adapter.in.web.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.reservation.dto.ReserveSeatResult;
import kr.hhplus.be.server.domain.reservation.domain.ReservationStatus;
import lombok.Builder;

@Builder
public record ReservationResponse(
	@Schema(description = "예약 ID")
	UUID reservationId,
	@Schema(description = "좌석 ID")
	UUID seatId,
	@Schema(description = "좌석 번호")
	Integer seatNo,
	@Schema(description = "좌석 가격")
	BigDecimal price,
	@Schema(description = "예약 상태")
	ReservationStatus status,
	@Schema(description = "예약 시간")
	LocalDateTime createdAt
) {
	public static ReservationResponse from(ReserveSeatResult result) {
		return ReservationResponse.builder()
			.reservationId(result.reservationId())
			.seatId(result.seatId())
			.seatNo(result.seatNo())
			.price(result.price())
			.status(result.status())
			.createdAt(result.createdAt())
			.build();
	}
}
