package kr.hhplus.be.server.api.concert.dto.request;

import java.util.UUID;

public record QueueTokenRequest (
	UUID userId
) {
}
