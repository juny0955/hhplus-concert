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
	LocalDateTime expireAt,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static Reservation of(UUID userId, UUID seatId) {
		return Reservation.builder()
			.userId(userId)
			.seatId(seatId)
			.status(ReservationStatus.PENDING)
			.expireAt(LocalDateTime.now().plusMinutes(5))
			.build();
	}

	public Reservation payment() {
		return Reservation.builder()
			.id(id)
			.userId(userId)
			.seatId(seatId)
			.status(ReservationStatus.SUCCESS)
			.updatedAt(LocalDateTime.now())
			.build();
	}

	public boolean isExpired() {
		return expireAt.isBefore(LocalDateTime.now());
	}
}
