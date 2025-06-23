package kr.hhplus.be.server.domain.seat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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

	public Seat expired() {
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

	public Seat reserve() {
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

	public boolean isAvailable() {
		return status.equals(SeatStatus.AVAILABLE);
	}

    public boolean isReserved() {
		return status == SeatStatus.RESERVED;
    }
}
