package kr.hhplus.be.server.reservation.domain.reservation;

import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
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

	public Reservation paid() throws CustomException {
		if (!status.equals(ReservationStatus.PENDING))
			throw new CustomException(ErrorCode.RESERVATION_STATUS_NOT_PENDING);

		return Reservation.builder()
			.id(id)
			.userId(userId)
			.seatId(seatId)
			.status(ReservationStatus.SUCCESS)
			.createdAt(createdAt)
			.updatedAt(LocalDateTime.now())
			.build();
	}

	public Reservation expired() throws CustomException {
		if (!status.equals(ReservationStatus.PENDING))
			throw new CustomException(ErrorCode.RESERVATION_STATUS_NOT_PENDING);

		return Reservation.builder()
				.id(id)
				.userId(userId)
				.seatId(seatId)
				.status(ReservationStatus.FAILED)
				.createdAt(createdAt)
				.updatedAt(LocalDateTime.now())
				.build();
	}
}
