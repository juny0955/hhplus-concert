package kr.hhplus.be.server.reservation.adapter.in.web.reservation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReservationResponse(
	@Schema(description = "예약 ID")
	UUID reservationId,
	@Schema(description = "좌석 ID")
	UUID seatId,
	@Schema(description = "예약 상태")
	ReservationStatus status,
	@Schema(description = "예약 시간")
	LocalDateTime createdAt
) {
	public static ReservationResponse from(Reservation reservation) {
		return ReservationResponse.builder()
			.reservationId(reservation.id())
			.seatId(reservation.seatId())
			.status(reservation.status())
			.createdAt(reservation.createdAt())
			.build();
	}
}
