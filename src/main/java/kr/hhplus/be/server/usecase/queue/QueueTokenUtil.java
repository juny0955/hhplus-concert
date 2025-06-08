package kr.hhplus.be.server.usecase.queue;

import java.util.UUID;

import kr.hhplus.be.server.domain.queue.QueueToken;
import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueueTokenUtil {

	private static final String ACTIVE_TOKEN_COUNT_KEY = "queue:active:count:%s";
	private static final String WAITING_TOKEN_KEY = "queue:waiting:%s";
	private static final String TOKEN_INFO_KEY = "token:info:%s";
	private static final String TOKEN_ID_KEY = "token:id:%s:%s";

	public static void validateQueueToken(QueueToken queueToken) throws CustomException {
		if (queueToken == null || !queueToken.isActive())
			throw new CustomException(ErrorCode.INVALID_QUEUE_TOKEN);
	}

	public static String formattingTokenIdKey(UUID userId, UUID concertId) {
		return String.format(TOKEN_ID_KEY, userId, concertId);
	}

	public static String formattingTokenInfoKey(UUID tokenId) {
		return String.format(TOKEN_INFO_KEY, tokenId);
	}

	public static String formattingActiveCountKey(UUID concertId) {
		return String.format(ACTIVE_TOKEN_COUNT_KEY, concertId);
	}

	public static String formattingWaitingTokenKey(UUID concertId) {
		return String.format(WAITING_TOKEN_KEY, concertId);
	}
}
