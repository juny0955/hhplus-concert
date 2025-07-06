package kr.hhplus.be.server.queue.domain;

import java.util.UUID;

import org.springframework.data.redis.core.script.DefaultRedisScript;

import kr.hhplus.be.server.framework.exception.CustomException;
import kr.hhplus.be.server.framework.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueueTokenUtil {

	private static final String ACTIVE_TOKEN_KEY = "queue:active:%s";
	private static final String WAITING_TOKEN_KEY = "queue:waiting:%s";
	private static final String TOKEN_INFO_KEY = "token:info:%s";
	private static final String TOKEN_ID_KEY = "token:id:%s:%s";

	public static void validateActiveQueueToken(QueueToken queueToken) throws CustomException {
		if (queueToken == null || !queueToken.isActive())
			throw new CustomException(ErrorCode.INVALID_QUEUE_TOKEN);
	}

	public static String formattingTokenIdKey(UUID userId, UUID concertId) {
		return String.format(TOKEN_ID_KEY, userId, concertId);
	}

	public static String formattingTokenInfoKey(UUID tokenId) {
		return String.format(TOKEN_INFO_KEY, tokenId);
	}

	public static String formattingActiveTokenKey(UUID concertId) {
		return String.format(ACTIVE_TOKEN_KEY, concertId);
	}

	public static String formattingWaitingTokenKey(UUID concertId) {
		return String.format(WAITING_TOKEN_KEY, concertId);
	}

	public static DefaultRedisScript<Long> promoteWaitingTokenScript() {
		DefaultRedisScript<Long> script = new DefaultRedisScript<>();
		script.setScriptText(PROMOTE_WAITING_TOKEN_SCRIPT);

		return script;
	}

	public static final String PROMOTE_WAITING_TOKEN_SCRIPT = """
			local activeTokenKey = KEYS[1]
			local waitingTokenKey = KEYS[2]
			local maxActiveTokenSize = tonumber(ARGV[1])
			
			-- 활성 토큰 개수 조회
			local activeCount = redis.call('SCARD', activeTokenKey)
			local leftActiveCount = maxActiveTokenSize - activeCount
			
			if leftActiveCount <= 0 then
			    return 0
			end
			
			-- 남은 활성 토큰 개수 만큼 대기 토큰 조회
			local waitingTokens = redis.call('ZRANGE', waitingTokenKey, 0, leftActiveCount - 1)
			if #waitingTokens == 0 then
			    return 0
			end
			
			-- 대기 토큰 활성 토큰으로 승급
			for i, tokenId in ipairs(waitingTokens) do
			    redis.call('SADD', activeTokenKey, tokenId)
			    redis.call('ZREM', waitingTokenKey, tokenId)
			end
			return #waitingTokens
			""";
}
