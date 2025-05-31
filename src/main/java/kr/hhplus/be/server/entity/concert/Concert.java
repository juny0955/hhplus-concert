package kr.hhplus.be.server.entity.concert;

import java.time.LocalDateTime;
import java.util.UUID;

public record Concert(
	UUID id,
	String title,
	String artist,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
