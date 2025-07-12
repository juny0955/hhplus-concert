package kr.hhplus.be.server.concert.domain.seat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Builder;

@Builder
public record Seat(
	UUID id,
	UUID concertDateId,
	int seatNo,
	BigDecimal price,
	SeatClass seatClass,
	SeatStatus status,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public Seat expire() throws CustomException {
		if (!status.equals(SeatStatus.RESERVED))
			throw new CustomException(ErrorCode.SEAT_STATUS_NOT_RESERVED);

		return Seat.builder()
				.id(id)
				.concertDateId(concertDateId)
				.seatNo(seatNo)
				.price(price)
				.seatClass(seatClass)
				.status(SeatStatus.AVAILABLE)
				.updatedAt(LocalDateTime.now())
				.build();
	}

	public Seat reserve() throws CustomException {
		if (!status.equals(SeatStatus.AVAILABLE))
			throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);

		return Seat.builder()
			.id(id)
			.concertDateId(concertDateId)
			.seatNo(seatNo)
			.price(price)
			.seatClass(seatClass)
			.status(SeatStatus.RESERVED)
			.updatedAt(LocalDateTime.now())
			.build();
	}

	public Seat payment() {
		return Seat.builder()
			.id(id)
			.concertDateId(concertDateId)
			.seatNo(seatNo)
			.price(price)
			.seatClass(seatClass)
			.status(SeatStatus.ASSIGNED)
			.updatedAt(LocalDateTime.now())
			.build();
	}
}
