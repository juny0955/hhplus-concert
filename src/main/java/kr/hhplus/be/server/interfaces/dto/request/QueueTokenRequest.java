package kr.hhplus.be.server.interfaces.dto.request;

import java.util.UUID;

public record QueueTokenRequest (
	UUID userId
) {
}
