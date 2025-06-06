package kr.hhplus.be.server.domain.reservation;

import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.interfaces.gateway.repository.reservation.ReservationEntity;
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
	public static Reservation of(UUID userId, UUID seatId) {
		return Reservation.builder()
			.userId(userId)
			.seatId(seatId)
			.status(ReservationStatus.PENDING)
			.build();
	}

	public static Reservation from(ReservationEntity save) {
		return Reservation.builder()
			.id(UUID.fromString(save.getId()))
			.userId(UUID.fromString(save.getUserId()))
			.seatId(UUID.fromString(save.getSeatId()))
			.status(save.getStatus())
			.createdAt(save.getCreatedAt())
			.updatedAt(save.getUpdatedAt())
			.build();
	}
}
