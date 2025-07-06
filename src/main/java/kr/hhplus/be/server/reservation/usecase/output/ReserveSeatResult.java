package kr.hhplus.be.server.reservation.usecase.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.reservation.domain.ReservationStatus;
import kr.hhplus.be.server.infrastructure.persistence.reservation.CreateReservationResult;
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
	public static ReserveSeatResult from(CreateReservationResult result) {
		return ReserveSeatResult.builder()
			.reservationId(result.reservation().id())
			.seatId(result.seat().id())
			.seatNo(result.seat().seatNo())
			.price(result.seat().price())
			.status(result.reservation().status())
			.createdAt(LocalDateTime.now())
			.build();
	}
}
