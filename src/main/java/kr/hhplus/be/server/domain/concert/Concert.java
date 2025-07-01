package kr.hhplus.be.server.domain.concert;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record Concert(
	UUID id,
	String title,
	String artist,
	LocalDateTime openTime,
	LocalDateTime soldOutTime,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public boolean isOpen() {
		return openTime.isAfter(LocalDateTime.now());
	}
}
