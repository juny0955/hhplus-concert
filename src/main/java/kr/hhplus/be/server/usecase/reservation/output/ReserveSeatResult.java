package kr.hhplus.be.server.usecase.reservation.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.concert.dto.response.ReservationResponse;

public record ReserveSeatResult(
	UUID reservationId,
	UUID seatId,
	Integer seatNo,
	BigDecimal price,
	ReservationStatus status,
	LocalDateTime createdAt
) {
	public ReservationResponse toResponse() {
		return new ReservationResponse(reservationId, seatId, seatNo, price, status, createdAt);
	}
}
