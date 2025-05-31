package kr.hhplus.be.server.interfaces.api.concert.concert.dto.request;

import java.util.UUID;

public record QueueTokenRequest (
	UUID userId
) {
}
