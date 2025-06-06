package kr.hhplus.be.server.domain.reservation;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record Reservation(
	UUID id,
	UUID userId,
	UUID seatId,
	ReservationStatus status,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static Reservation of(UUID userId, UUID seatId, LocalDateTime now) {
		return Reservation.builder()
			.userId(userId)
			.seatId(seatId)
			.status(ReservationStatus.PENDING)
			.createdAt(now)
			.updatedAt(now)
			.build();
	}
}
