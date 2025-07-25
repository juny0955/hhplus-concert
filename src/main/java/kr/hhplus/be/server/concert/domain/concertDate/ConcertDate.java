package kr.hhplus.be.server.concert.domain.concertDate;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record ConcertDate(
	UUID id,
	UUID concertId,
	Integer remainingSeatCount,
	LocalDateTime date,
	LocalDateTime deadline,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public boolean checkDeadline() {
		return deadline.isBefore(LocalDateTime.now());
	}
}
