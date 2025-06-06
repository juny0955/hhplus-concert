package kr.hhplus.be.server.usecase.reservation.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.interfaces.api.reservation.dto.response.ReservationResponse;
import lombok.Builder;

@Builder
public record ReserveSeatResult(
	UUID reservationId,
	UUID seatId,
	Integer seatNo,
	BigDecimal price,
	ReservationStatus status,
	LocalDateTime createdAt
) {
	public static ReserveSeatResult of(UUID reservationId, UUID seatId, Integer seatNo, BigDecimal price, ReservationStatus status) {
		return ReserveSeatResult.builder()
			.reservationId(reservationId)
			.seatId(seatId)
			.seatNo(seatNo)
			.price(price)
			.status(status)
			.createdAt(LocalDateTime.now())
			.build();
	}

	public ReservationResponse toResponse() {
		return new ReservationResponse(reservationId, seatId, seatNo, price, status, createdAt);
	}
}
