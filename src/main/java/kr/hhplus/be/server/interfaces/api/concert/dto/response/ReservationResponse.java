package kr.hhplus.be.server.interfaces.api.concert.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;

public record ReservationResponse(
	@Schema(description = "예약 ID")
	UUID reservationId,
	@Schema(description = "좌석 ID")
	UUID seatId,
	@Schema(description = "결제 ID")
	UUID paymentId,
	@Schema(description = "좌석 번호")
	Integer seatNo,
	@Schema(description = "좌석 가격")
	BigDecimal price,
	@Schema(description = "예약 상태")
	ReservationStatus status,
	@Schema(description = "예약 시간")
	LocalDateTime createdAt
) {
}
