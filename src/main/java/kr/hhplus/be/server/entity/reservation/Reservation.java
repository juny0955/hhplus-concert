package kr.hhplus.be.server.entity.reservation;

import java.time.LocalDateTime;
import java.util.UUID;

public record Reservation(
	UUID id,
	UUID userId,
	UUID seatId,
	ReservationStatus status,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
